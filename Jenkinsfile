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

codeCovTokenId = "psdm-codecov-token"

//// git webhook trigger token
//// http://JENKINS_URL/generic-webhook-trigger/invoke?token=<webhookTriggerToken>
webhookTriggerToken = "b0ba1564ca8c4d12ffun639b160d2ek6h9bauhk86"

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
                    currentBuild.displayName = "release deployment"

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
                    }

                    // post processing
                    stage('post processing') {
                        // publish reports
                        publishReports()

                        // inform codecov.io
                        withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                            // call codecov
                            sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                        }

                        // notify rocket chat about success
                        rocketSend channel: rocketChatChannel, emoji: ':jenkins_party:',
                                message: "release deployment successfully! Please visit https://oss.sonatype.org/ " +
                                        "to stag and release the artifact!" +
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

        // notify rocket chat about the started feature branch run
        rocketSend channel: rocketChatChannel, emoji: ':jenkins_triggered:',
                message: "master branch build triggered (incl. snapshot deploy) by merging feature branch '${params.pull_request_head_ref}'\n"
        rawMessage: true

        node {
            ansiColor('xterm') {
                try {
                    // set java version
                    setJavaVersion(javaVersionId)

                    // set build display name
                    currentBuild.displayName = "merge pr ${params.pull_request_head_ref}"

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
                    }

                    // post processing
                    stage('post processing') {
                        // publish reports
                        publishReports()

                        // inform codecov.io
                        withCredentials([string(credentialsId: codeCovTokenId, variable: 'codeCovToken')]) {
                            // call codecov
                            sh "curl -s https://codecov.io/bash | bash -s - -t ${env.codeCovToken} -C ${commitHash}"
                        }

                        // notify rocket chat about success
                        String buildMode = "merge"
                        String branchName = params.pull_request_head_label

                        // notify rocket chat
                        rocketSend channel: rocketChatChannel, emoji: ':jenkins_party:',
                                message: "merged feature branch '${params.pull_request_head_ref}' successfully into " +
                                        "master and deployed to oss sonatype!\n" +
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
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "*branch:* master\n"
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
        def jsonObj = getGithubJsonObj(env.CHANGE_ID, orgNames.get(0), projects.get(0))

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
            [parameters(
                    [string(defaultValue: '', description: '', name: 'release', trim: true),
                     string(defaultValue: '', description: '', name: 'action', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_title', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_merged', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_state', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_base_ref', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_head_label', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_head_ref', trim: true),
                     string(defaultValue: '', description: '', name: 'repository_name', trim: true)
                    ]),
             [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
             [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 0, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project'],
             [$class: 'JiraProjectProperty'],
             pipelineTriggers(
                     [GenericTrigger(causeString: '$issue_title',
                             genericVariables:
                                     [[defaultValue: '', key: 'action', regexpFilter: '', value: '$.action'],
                                      [defaultValue: '', key: 'pull_request_merged', regexpFilter: '', value: '$.pull_request.merged'],
                                      [defaultValue: '', key: 'pull_request_state', regexpFilter: '', value: '$.pull_request.state'],
                                      [defaultValue: '', key: 'pull_request_base_ref', regexpFilter: '', value: '$.pull_request.base.ref'],
                                      [defaultValue: '', key: 'pull_request_title', regexpFilter: '', value: '$.pull_request.title'],
                                      [defaultValue: '', key: 'pull_request_head_label', regexpFilter: '', value: ' $.pull_request.head.label'],
                                      [defaultValue: '', key: 'pull_request_head_label', regexpFilter: '', value: ' $.pull_request.head.ref'],
                                      [defaultValue: '', key: 'repository_name', regexpFilter: '', value: '$.repository.name']],

                             printContributedVariables: true,
                             printPostContent: true,
                             regexpFilterExpression: '^(closed true closed master)$',
                             regexpFilterText: '$action $pull_request_merged $pull_request_state $pull_request_base_ref',
                             silentResponse: true,
                             token: webhookTriggerToken)])])
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

def getGithubJsonObj(String prId, String orgName, String repoName) {
    def jsonObj = readJSON text: curl(prId, orgName, repoName)
    return jsonObj
}


def curl(String prId, String orgName, String repoName) {

    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/pulls/" + prId
    String jsonResponseString = sh(script: curlUrl, returnStdout: true)

    return jsonResponseString
}