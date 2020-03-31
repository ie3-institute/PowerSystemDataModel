#!groovy​


////////////////////////////////
// general config values
////////////////////////////////


//// Rocket.Chat channel to publish updates
final String rocketChatChannel = "jenkins"


//// project build dir order
//// this list contains the build order
//// normally it *should* start with the project under investigation
//// but if this projects depends on some of our other projects, you have to *build the dependencies first*!
//// *IMPORTANT:* you *MUST* use exact repo names as this will used for checkout!
//// *IMPORTANT2:* you must provide exact 4 elements!
projects = ['powersystemdatamodel']

orgNames = ['ie3-institute']
urls = ['git@github.com:' + orgNames.get(0)]

def sonarqubeProjectKey = "edu.ie3:PowerSystemDataModel"

/// code coverage token id
codeCovTokenId = "psdm-codecov-token"

//// internal jenkins credentials link for git ssh keys
//// requires the ssh key to be stored in the internal jenkins credentials keystore
def sshCredentialsId = "19f16959-8a0d-4a60-bd1f-5adb4572b702"

//// internal maven central credentials
def mavenCentralCredentialsId = "87bfb2d4-7613-4816-9fe1-70dfd7e6dec2"

def mavenCentralSignKeyFileId = "dc96216c-d20a-48ff-98c0-1c7ba096d08d"

def mavenCentralSignKeyId = "a1357827-1516-4fa2-ab8e-72cdea07a692"

//// define and setjava version ////
//// requires the java version to be set in the internal jenkins java version management
//// use identifier accordingly
def javaVersionId = 'jdk-8'

//// set java version method (needs node{} for execution)
void setJavaVersion(javaVersionId) {
    env.JAVA_HOME = "${tool javaVersionId}"
    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
}

/// global config variables that should be available during runtime
/// and will be overwritten during runtime -> DO NOT CHANGE THEM
String featureBranchName = ""

//// gradle tasks that are executed
def gradleTasks = "--refresh-dependencies clean spotlessCheck pmdMain pmdTest spotbugsMain spotbugsTest allTests" // the gradle tasks that are executed on ALL projects
def mainProjectGradleTasks = "jacocoTestReport jacocoTestCoverageVerification" // additional tasks that are only executed on project 0 (== main project)
// if you need additional tasks for deployment add them here
// NOTE: artifactory task with credentials will be added below
def deployGradleTasks = ""

/// prepare debugging info about deployed artifacts
String deployedArtifacts = "none"

/// commit hash
def commitHash = ""

if (env.BRANCH_NAME == "master") {

    // setup
    getMasterBranchProps()

    // release deployment
    if (params.release == "true") {

        // notify rocket chat about the release deployment
        rocketSend channel: rocketChatChannel, emoji: ':jenkins_triggered:',
                message: "deploying release to oss sonatype. pls remember to stag and release afterwards!\n"
        rawMessage: true

        node {
            ansiColor('xterm') {
                try {
                    // set java version
                    setJavaVersion(javaVersionId)

                    // set build display name
                    currentBuild.displayName = "release deployment" + " (" + currentBuild.displayName + ")"

                    // checkout from scm
                    stage('checkout from scm') {
                        try {
                            // merged mode
                            commitHash = gitCheckout(projects.get(0), urls.get(0), 'refs/heads/master', sshCredentialsId).GIT_COMMIT
                        } catch (exc) {
                            sh 'exit 1' // failure due to not found master branch
                        }
                    }

                    // test the project
                    stage("gradle allTests ${projects.get(0)}") {
                        // build and test the project
                        gradle("${gradleTasks} ${mainProjectGradleTasks}")
                    }

                    // execute sonarqube code analysis
                    stage('SonarQube analysis') {
                        withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                            gradle("sonarqube -Dsonar.branch.name=master -Dsonar.projectKey=$sonarqubeProjectKey ")
                        }
                    }

                    // wait for the sonarqube quality gate
                    stage("Quality Gate") {
                        timeout(time: 1, unit: 'HOURS') {
                            // Just in case something goes wrong, pipeline will be killed after a timeout
                            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                            if (qg.status != 'OK') {
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                    }

                    // publish report und coverage 
                    stage('publish reports + coverage') {
                        // publish reports
                        publishReports()

                        // inform codecov.io
                        withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                            // call codecov
                            sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                        }

                    }

                    // deploy snapshot version to oss sonatype
                    stage('deploy release') {
                        // get the sonatype credentials stored in the jenkins secure keychain
                        withCredentials([usernamePassword(credentialsId: mavenCentralCredentialsId, usernameVariable: 'mavencentral_username', passwordVariable: 'mavencentral_password'),
                                         file(credentialsId: mavenCentralSignKeyFileId, variable: 'mavenCentralKeyFile'),
                                         usernamePassword(credentialsId: mavenCentralSignKeyId, passwordVariable: 'signingPassword', usernameVariable: 'signingKeyId')]) {
                            deployGradleTasks = "--refresh-dependencies clean allTests " + deployGradleTasks + "publish -Puser=${env.mavencentral_username} -Ppassword=${env.mavencentral_password} -Psigning.keyId=${env.signingKeyId} -Psigning.password=${env.signingPassword} -Psigning.secretKeyRingFile=${env.mavenCentralKeyFile}"

                            gradle("${deployGradleTasks} -Prelease")

                            deployedArtifacts = "${projects.get(0)}, "

                        }

                        // notify rocket chat
                        rocketSend channel: rocketChatChannel, emoji: ':jenkins_party:',
                                message: "release deployment successful. pls remember visiting https://oss.sonatype.org" +
                                        "too stag and release the artifact!" +
                                        "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                        "*branch:* master \n"
                        rawMessage: true
                    }

                } catch (Exception e) {
                    // set build result to failure
                    currentBuild.result = 'FAILURE'

                    // publish reports even on failure
                    publishReports()

                    // print exception
                    Date date = new Date()
                    println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + e)

                    // notify rocket chat
                    rocketSend channel: rocketChatChannel, emoji: ':jenkins_explode:',
                            message: "release deployment failed!\n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "*branch:* master\n"
                    rawMessage: true
                }

            }

        }


    } else {
        // merge of features

        node {
            ansiColor('xterm') {
                try {
                    // set java version
                    setJavaVersion(javaVersionId)

                    // checkout from scm
                    stage('checkout from scm') {
                        try {
                            // merged mode
                            commitHash = gitCheckout(projects.get(0), urls.get(0), 'refs/heads/master', sshCredentialsId).GIT_COMMIT
                        } catch (exc) {
                            sh 'exit 1' // failure due to not found master branch
                        }
                    }

                    // get information based on commit hash
                    def jsonObject = getGithubCommitJsonObj(commitHash, orgNames.get(0), projects.get(0))
                    featureBranchName = splitStringToBranchName(jsonObject.commit.message)

                    def message = (featureBranchName?.trim()) ?
                            "master branch build triggered (incl. snapshot deploy) by merging pr from feature branch '${featureBranchName}'"
                            : "master branch build triggered (incl. snapshot deploy) for commit with message '${jsonObject.commit.message}'"

                    // notify rocket chat about the started feature branch run
                    rocketSend channel: rocketChatChannel, emoji: ':jenkins_triggered:',
                            message: message + "\n"
                    rawMessage: true

                    // set build display name
                    currentBuild.displayName = ((featureBranchName?.trim()) ? "merge pr branch '${featureBranchName}'" : "commit '" +
                            "${jsonObject.commit.message.length() <= 20 ? jsonObject.commit.message : jsonObject.commit.message.substring(0, 20)}...'") + " (" + currentBuild.displayName + ")"


                    // test the project
                    stage("gradle allTests ${projects.get(0)}") {
                        // build and test the project
                        gradle("${gradleTasks} ${mainProjectGradleTasks}")
                    }

                    // execute sonarqube code analysis
                    stage('SonarQube analysis') {
                        withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                            gradle("sonarqube -Dsonar.branch.name=master -Dsonar.projectKey=$sonarqubeProjectKey ")
                        }
                    }


                    // wait for the sonarqube quality gate
                    stage("Quality Gate") {
                        timeout(time: 1, unit: 'HOURS') {
                            // Just in case something goes wrong, pipeline will be killed after a timeout
                            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                            if (qg.status != 'OK') {
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                    }

                    // post processing
                    stage('publish reports + coverage') {
                        // publish reports
                        publishReports()

                        // inform codecov.io
                        withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                            // call codecov
                            sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                        }

                    }


                    // deploy snapshot version to oss sonatype
                    stage('deploy') {
                        // get the sonatype credentials stored in the jenkins secure keychain
                        withCredentials([usernamePassword(credentialsId: mavenCentralCredentialsId, usernameVariable: 'mavencentral_username', passwordVariable: 'mavencentral_password'),
                                         file(credentialsId: mavenCentralSignKeyFileId, variable: 'mavenCentralKeyFile'),
                                         usernamePassword(credentialsId: mavenCentralSignKeyId, passwordVariable: 'signingPassword', usernameVariable: 'signingKeyId')]) {
                            deployGradleTasks = "--refresh-dependencies clean allTests " + deployGradleTasks + "publish -Puser=${env.mavencentral_username} -Ppassword=${env.mavencentral_password} -Psigning.keyId=${env.signingKeyId} -Psigning.password=${env.signingPassword} -Psigning.secretKeyRingFile=${env.mavenCentralKeyFile}"

                            gradle("${deployGradleTasks}")

                            deployedArtifacts = "${projects.get(0)}, "

                        }

                        // notify rocket chat
                        message = (featureBranchName?.trim()) ?
                                "master branch build successful! Merged pr from feature branch '${featureBranchName}'"
                                : "master branch build successful! Build commit with message is '${jsonObject.commit.message}'"
                        rocketSend channel: rocketChatChannel, emoji: ':jenkins_party:',
                                message: message + "\n" +
                                        "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                        "*branch:* master \n"
                        rawMessage: true

                    }


                } catch (Exception e) {
                    // set build result to failure
                    currentBuild.result = 'FAILURE'

                    // publish reports even on failure
                    publishReports()

                    // print exception
                    Date date = new Date()
                    println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + e)

                    // notify rocket chat
                    rocketSend channel: rocketChatChannel, emoji: ':jenkins_explode:',
                            message: "merge feature into master failed!\n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n"
                    rawMessage: true
                }

            }

        }

    }

} else {

    // setup
    getFeatureBranchProps()

    node {
        // curl the api to get debugging details
        def jsonObj = getGithubPRJsonObj(env.CHANGE_ID, orgNames.get(0), projects.get(0))

        // This displays colors using the 'xterm' ansi color map.
        ansiColor('xterm') {
            try {
                // set java version
                setJavaVersion(javaVersionId)

                /// set the build name
                featureBranchName = jsonObj.head.ref
                currentBuild.displayName = featureBranchName + " (" + currentBuild.displayName + ")"

                // notify rocket chat about the started feature branch run
                rocketSend channel: rocketChatChannel, emoji: ':jenkins_triggered:',
                        message: "feature branch build triggered:\n" +
                                "*repo:* ${jsonObj.head.repo.full_name}\n" +
                                "*branch:* ${featureBranchName}\n"
                rawMessage: true

                stage('checkout from scm') {

                    try {
                        commitHash = gitCheckout(projects.get(0), urls.get(0), featureBranchName, sshCredentialsId).GIT_COMMIT
                    } catch (exc) {
                        // our target repo failed during checkout
                        sh 'exit 1' // failure due to not found forcedPR branch
                    }

                }

                // test the project
                stage("gradle allTests ${projects.get(0)}") {

                    // build and test the project
                    gradle("${gradleTasks} ${mainProjectGradleTasks}")
                }

                // execute sonarqube code analysis
                stage('SonarQube analysis') {
                    withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                        gradle("sonarqube -Dsonar.projectKey=$sonarqubeProjectKey -Dsonar.pullrequest.branch=${featureBranchName} -Dsonar.pullrequest.key=${env.CHANGE_ID} -Dsonar.pullrequest.base=master -Dsonar.pullrequest.github.repository=${orgNames.get(0)}/${projects.get(0)} -Dsonar.pullrequest.provider=Github")
                    }
                }

                // wait for the sonarqube quality gate
                stage("Quality Gate") {
                    timeout(time: 1, unit: 'HOURS') {
                        // Just in case something goes wrong, pipeline will be killed after a timeout
                        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }

                // post processing
                stage('post processing') {
                    // publish reports
                    publishReports()

                    withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                        // call codecov
                        sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                    }

                    // notify rocket chat
                    rocketSend channel: rocketChatChannel, emoji: ':jenkins_party:',
                            message: "feature branch test successful!\n" +
                                    "*repo:* ${jsonObj.head.repo.full_name}\n" +
                                    "*branch:* ${featureBranchName}\n"
                    rawMessage: true
                }
            } catch (Exception e) {
                // set build result to failure
                currentBuild.result = 'FAILURE'

                // publish reports even on failure
                publishReports()

                // print exception
                Date date = new Date()
                println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + e)

                // notify rocket chat
                rocketSend channel: rocketChatChannel, emoji: ':jenkins_explode:',
                        message: "feature branch test failed!\n" +
                                "*repo:* ${jsonObj.head.repo.full_name}\n" +
                                "*branch:* ${featureBranchName}\n"
                rawMessage: true
            }

        }
    }
}


def getFeatureBranchProps() {

    properties(
            [pipelineTriggers([
                    issueCommentTrigger('.*!test.*')])
            ])

}


def getMasterBranchProps() {
    properties(
            [[$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
             [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 0, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project']
            ])
}


////////////////////////////////////
// git checkout
// NOTE: requires node {}
////////////////////////////////////
def gitCheckout(String relativeTargetDir, String baseUrl, String branch, String sshCredentialsId) {
    checkout([
            $class                           : 'GitSCM',
            branches                         : [[name: branch]],
            doGenerateSubmoduleConfigurations: false,
            extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]],
            submoduleCfg                     : [],
            userRemoteConfigs                : [[credentialsId: sshCredentialsId, url: baseUrl + "/" + relativeTargetDir + ".git"]]
    ])
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// publish reports
// IMPORTANT: has to be called inside the same node{} as where the build process (report generation) took place!
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
def publishReports() {
    // publish test reports
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/tests/allTests', reportFiles: 'index.html', reportName: "${projects.get(0)}_java_tests_report", reportTitles: ''])

    // publish jacoco report for main project only
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/jacoco', reportFiles: 'index.html', reportName: "${projects.get(0)}_jacoco_report", reportTitles: ''])

    // publish pmd report for main project only
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/pmd', reportFiles: 'main.html', reportName: "${projects.get(0)}_pmd_report", reportTitles: ''])

    // publish spotbugs report for main project only
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, escapeUnderscores: false, keepAll: true, reportDir: projects.get(0) + '/build/reports/spotbugs', reportFiles: 'main.html', reportName: "${projects.get(0)}_spotbugs_report", reportTitles: ''])

}


// gradle wrapper method for easy execution
// requires the gradle version to be configured with the same name under tools in jenkins configuration
def gradle(String command) {
    env.JENKINS_NODE_COOKIE = 'dontKillMe' // this is necessary for the Gradle daemon to be kept alive

    // switch directory to bew able to use gradle wrapper
    sh """cd ${projects.get(0)}""" + ''' set +x; ./gradlew ''' + """$command"""
}

def getGithubPRJsonObj(String prId, String orgName, String repoName) {
    def jsonObj = readJSON text: curlByPR(prId, orgName, repoName)
    return jsonObj
}


def curlByPR(String prId, String orgName, String repoName) {

    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/pulls/" + prId
    String jsonResponseString = sh(script: curlUrl, returnStdout: true)

    return jsonResponseString
}

def getGithubCommitJsonObj(String commit_sha, String orgName, String repoName) {
    def jsonObj = readJSON text: curlByCSHA(commit_sha, orgName, repoName)
    return jsonObj
}

def curlByCSHA(String commit_sha, String orgName, String repoName) {

    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/commits/" + commit_sha
    String jsonResponseString = sh(script: curlUrl, returnStdout: true)

    return jsonResponseString
}

def splitStringToBranchName(String string) {
    def obj = string.split().find { it.startsWith("ie3-institute") }
    if (obj)
        return (obj as String).substring(14)
    else
        return ""
}
