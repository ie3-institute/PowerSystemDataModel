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

//// git webhook trigger token
//// http://JENKINS_URL/generic-webhook-trigger/invoke?token=<webhookTriggerToken>
webhookTriggerToken = "b0ba1564ca8c4d12ffun639b160d2ek6h9bauhk86"

//// internal jenkins credentials link for git ssh keys
//// requires the ssh key to be stored in the internal jenkins credentials keystore
def sshCredentialsId = "5470eb14-b7a1-4247-baba-1e0f9a907666"

//// internal maven central credentials
def mavenCentralCredentialsId = "87bfb2d4-7613-4816-9fe1-70dfd7e6dec2"

def mavenCentralSignKeyFileId = "dc96216c-d20a-48ff-98c0-1c7ba096d08d"

def mavenCentralSignKeyId = "a1357827-1516-4fa2-ab8e-72cdea07a692"

//// define and setjava version ////
//// requires the java version to be set in the internal jenkins java version management
//// use identifier accordingly
def javaVersionId = 'jdk-11'

//// set java version method (needs node{} for execution)
def setJavaVersion(javaVersionId) {
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

//// error message catch variable
String stageErrorMessage = "Caught error without setting the errorMessage -> new error!"

/// prepare debugging info about deployed artifacts
String deployedArtifacts = "none"

//////////////////
// logger values
//////////////////

def w = "[WARN]"
def e = "[ERROR]"
def i = "[INFO]"

Date date = new Date()
datePart = date.format("dd/MM/yyyy")
timePart = date.format("HH:mm:ss")

def p(String logLevel) {
    "${logLevel} [${datePart} - ${timePart}] "
}

def log(String level, String message) {
    println(p(level) + message)
}

// disable scanning but load config parameters before
//if (isBranchIndexingCause()) {
//    println(env.BUILD_NUMBER)
//    println(currentBuild)
//    if (env.BUILD_NUMBER == 1) {
//        if (env.BRANCH_NAME == "master") {
//            getMasterBranchProps()
//        } else {
//            getFeatureBranchProps(resolveBranchNo(env.BRANCH_NAME))
//        }
//        currentBuild.result = 'FAILURE'
//    }
//    return
//}

/////////////////////////
// master branch script
/////////////////////////
if (env.BRANCH_NAME == "master") {
    getMasterBranchProps()

    // deployment only
    if (params.triggered == "true") {
        // notify rocket chat about the started master branch deployment
        rocketSend attachments: [],
                channel: rocketChatChannel,
                message: ":jenkins_triggered:\n" +
                        "*deploy* manually triggered on master branch of ${projects.get(0)}."
        rawMessage: true

        node {
            // This displays colors using the 'xterm' ansi color map.
            ansiColor('xterm') {
                try {
                    // set java version
                    setJavaVersion(javaVersionId)
                    // get the artifactory credentials stored in the jenkins secure keychain
                    withCredentials([usernamePassword(credentialsId: mavenCentralCredentialsId, usernameVariable: 'mavencentral_username', passwordVariable: 'mavencentral_password'),
                                     file(credentialsId: mavenCentralSignKeyFileId, variable: 'mavenCentralKeyFile'),
                                     usernamePassword(credentialsId: mavenCentralSignKeyId, passwordVariable: 'signingPassword', usernameVariable: 'signingKeyId')]) {
                        deployGradleTasks = "--refresh-dependencies clean allTests " + deployGradleTasks + "publish -Puser=${env.mavencentral_username} -Ppassword=${env.mavencentral_password} -Psigning.keyId=${env.signingKeyId} -Psigning.password=${env.signingPassword} -Psigning.secretKeyRingFile=${env.mavenCentralKeyFile}"

                        stage('checkout from scm') {
                            // first we try to get branches from each repo
                            // the checkout is done in parallel to speed up things a bit

                            log(i, "getting ${projects.get(0)} ...")
                            try {
                                log(i, "Deploy mode. Trying to get ${projects.get(0)} master branch ...")
                                gitCheckout(projects.get(0), urls.get(0), 'refs/heads/master', sshCredentialsId)
                            } catch (exc) {
                                // our target repo failed during checkout
                                stageErrorMessage = "checkout of master branch of ${projects.get(0)} repo failed!"
                                sh 'exit 1' // failure due to not found master branch
                            }

                        }

                        stage('deploy') {
                            log(i, "Deploying ${projects.get(0)} to maven central ...")
                            gradle("-p ${projects.get(0)} ${deployGradleTasks}")

                            deployedArtifacts = "${projects.get(0)}, "
                        }

                        /**
                         * Post processing
                         * Publish reports and notify rocket chat
                         * Future clean workspace processes should be declared here
                         */
                        stage('post processing') {
                            // publish reports
                            // publishReports()

                            // notify rocket chat about success
                            String buildMode = "deploy"
                            String branchName = params.pull_request_head_label

                            rocketSend attachments: [
                                    [$class: 'MessageAttachment', color: 'green', title: 'go to logs', titleLink: env.BUILD_URL],],
                                    channel: rocketChatChannel,
                                    message: ":jenkins_party: \n" +
                                            "${buildMode} successful!\n" +
                                            "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                            "*branch:* ${branchName} \n" +
                                            "*deployedArtifacts:* ${deployedArtifacts}\n"
                            rawMessage: true

                            // set build to successfull
                            currentBuild.result = 'SUCCESS'
                        }


                    }

                } catch (Exception exc) {
                    currentBuild.result = 'FAILURE'

                    String buildMode = "deploy"
                    String branchName = params.pull_request_head_label

                    // notify rocketchat about failure
                    rocketSend attachments: [
                            [$class: 'MessageAttachment', color: 'red', title: 'go to logs', titleLink: env.BUILD_URL],],
                            channel: rocketChatChannel,
                            message: ":jenkins_explode: \n" +
                                    "${buildMode}  failed!\n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "*branch:* ${branchName} \n" +
                                    "*errorMessage:* ${stageErrorMessage}\n" +
                                    "See logs for details.",
                            rawMessage: true

                    // publish reports even on failure
                    publishReports()

                    // print the error message that might be thrown during one of the build stages
                    error(stageErrorMessage)

                }
            }
        }
    } else {
        // merge mode
        // disable scan
//        if (params.pull_request_title == "") {
//            currentBuild.result = 'SUCCESS'
//            return
//        }

        // merge into master
        // notify rocket chat about the started master branch deployment
        rocketSend attachments: [],
                channel: rocketChatChannel,
                message: ":jenkins_triggered:\n" +
                        "*deploy* triggered by merge ${params.pull_request_title} into master of ${projects.get(0)}."
        rawMessage: true

        node {
            // This displays colors using the 'xterm' ansi color map.
            ansiColor('xterm') {
                try {
                    // set java version
                    setJavaVersion(javaVersionId)

                    /// set build name
                    currentBuild.displayName = "merge pr ${params.pull_request_title}"

                    /// set the the branch name
                    /// this is necessary even in merged mode as we might depend on other unmerged branches in other dependency repos with the to-develop feature(s)
                    featureBranchName = params.pull_request_title

                    stage('checkout from scm') {
                        // first we try to get branches from each repo
                        // the checkout is done in parallel to speed up things a bit

                        log(i, "getting ${projects.get(0)} ...")
                        try {
                            // merged mode
                            log(i, "Merged mode. Trying to get ${projects.get(0)} master branch ...")
                            gitCheckout(projects.get(0), urls.get(0), 'refs/heads/master', sshCredentialsId)

                        } catch (exc) {
                            // our target repo failed during checkout
                            stageErrorMessage = "checkout of master branch of ${projects.get(0)} repo failed!"
                            sh 'exit 1' // failure due to not found master branch
                        }
                    }

                    // the first stage should always be the mainProject -> if it fails we can skip the rest!
                    stage("gradle allTests ${projects.get(0)} with included builds") {

                        // display java version
                        sh "java -version"

                        // build and test the first project
                        log(i, "building and testing ${projects.get(0)}")
                        gradle("-p ${projects.get(0)} ${gradleTasks} ${mainProjectGradleTasks}")
                    }

                    stage('SonarQube analysis') {
                        withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                            gradle("-p ${projects.get(0)} sonarqube -Dsonar.branch.name=master -Dsonar.projectKey=$sonarqubeProjectKey ")
                        }
                    }

                    stage("Quality Gate") {
                        timeout(time: 1, unit: 'HOURS') {
                            // Just in case something goes wrong, pipeline will be killed after a timeout
                            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                            if (qg.status != 'OK') {
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                    }


                    /**
                     * Deploy to ie3 artifactory making use of the artifactory credentials stored in jenkins secure keychain.
                     */
                    stage('deploy') {
                        // get the artifactory credentials stored in the jenkins secure keychain
                        withCredentials([usernamePassword(credentialsId: mavenCentralCredentialsId, usernameVariable: 'mavencentral_username', passwordVariable: 'mavencentral_password'),
                                         file(credentialsId: mavenCentralSignKeyFileId, variable: 'mavenCentralKeyFile'),
                                         usernamePassword(credentialsId: mavenCentralSignKeyId, passwordVariable: 'signingPassword', usernameVariable: 'signingKeyId')]) {
                            deployGradleTasks = "--refresh-dependencies clean allTests " + deployGradleTasks + "publish -Puser=${env.mavencentral_username} -Ppassword=${env.mavencentral_password} -Psigning.keyId=${env.signingKeyId} -Psigning.password=${env.signingPassword} -Psigning.secretKeyRingFile=${env.mavenCentralKeyFile}"


                            log(i, "Deploying ${projects.get(0)} to maven central ...")
                            gradle("-p ${projects.get(0)} --parallel ${deployGradleTasks}")

                            deployedArtifacts = "${projects.get(0)}, "

                        }
                    }

                    /**
                     * Post processing
                     * Publish reports and notify rocket chat
                     * Future clean workspace processes should be declared here
                     */
                    stage('post processing') {
                        // publish reports
                        publishReports()

                        // notify rocket chat about success
                        String buildMode = "merge"
                        String branchName = params.pull_request_head_label

                        rocketSend attachments: [
                                [$class: 'MessageAttachment', color: 'green', title: 'go to logs', titleLink: env.BUILD_URL],],
                                channel: rocketChatChannel,
                                message: ":jenkins_party: \n" +
                                        "${buildMode} successful!\n" +
                                        "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                        "*branch:* ${branchName} \n" +
                                        "*deployedArtifacts:* ${deployedArtifacts}\n"
                        rawMessage: true

                    }

                } catch (exc) {
                    currentBuild.result = 'FAILURE'

                    String buildMode = "merge"
                    String branchName = params.pull_request_head_label

                    if (stageErrorMessage == 'Not a merge. Abort!') {
                        buildMode = ""
                    }

                    // notify rocketchat about failure
                    rocketSend attachments: [
                            [$class: 'MessageAttachment', color: 'red', title: 'go to logs', titleLink: env.BUILD_URL],],
                            channel: rocketChatChannel,
                            message: ":jenkins_explode: \n" +
                                    "${buildMode}  failed!\n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "*branch:* ${branchName} \n" +
                                    "*errorMessage:* ${stageErrorMessage}\n" +
                                    "See logs for details.",
                            rawMessage: true

                    // publish reports even on failure
                    publishReports()

                    // print the error message that might be thrown during one of the build stages
                    error(stageErrorMessage)

                    // print the error message that might be thrown during one of the build stages
                    error(exc)
                }

            }
        }
    }


} else {
/////////////////////////
// feature branch script
/////////////////////////
    node {
        // resolve branch name and configure the project
        // which requires a node
        getFeatureBranchProps(resolveBranchNo(env.BRANCH_NAME))

        // disable scan
//        if (params.triggered != "true" && params.comment_body != "!test") {
//
//            log(i, "Scan mode. Doing nothing!")
//            currentBuild.result = 'FAILURE'
//            // signals github that this branch hasn't build yet -> fail before first build
//            return
//        }

        // This displays colors using the 'xterm' ansi color map.
        ansiColor('xterm') {
            try {
                // set java version
                setJavaVersion(javaVersionId)

                /// set the the branch name
                featureBranchName = resolveBranchName(env.BRANCH_NAME, orgNames.get(0), projects.get(0))

                /// set build name
                currentBuild.displayName = featureBranchName + "_${params.sender_login}"

                /// if the pipeline is scanned we don't want to execute anything
                if (params.triggered == "true") {
                    // notify rocket chat about the started master branch deployment
                    rocketSend attachments: [],
                            channel: rocketChatChannel,
                            message: ":jenkins_triggered:\n" +
                                    "*PR test* manually triggered on master branch of ${projects.get(0)}."
                    rawMessage: true

                    log(i, "PR test manually triggered by user.")

                } else {
                    // notify rocket chat about the started PR run
                    rocketSend attachments: [],
                            channel: rocketChatChannel,
                            message: ":jenkins_triggered:\n" +
                                    "*forcedPR* triggered with parameters:\n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "*branch:* ${params.issue_title}\n" +
                                    "*triggeredBy:* ${params.sender_login}",
                            rawMessage: true

                    log(i, "forced PR triggered by webhook. Parameters are:")
                    log(i, "issue_title: ${params.issue_title}")
                    log(i, "comment_body: ${params.comment_body}")
                    log(i, "action: ${params.action}")
                }


                stage('checkout from scm') {

                    // first we try to get branches from each repo
                    // we try to get the PR branch name from our target repo if this fails we abort, for all other repos we try to get either the forcedPR branch OR the master branch
                    // the checkout is done in parallel to speed up things a bit

                    log(i, "getting ${projects.get(0)} ...")
                    try {
                        log(i, "ForcedPR mode. Try to get ${featureBranchName} branch ...")
                        gitCheckout(projects.get(0), urls.get(0), featureBranchName, sshCredentialsId)
                    } catch (exc) {
                        // our target repo failed during checkout
                        stageErrorMessage = "branch ${featureBranchName} not found in ${projects.get(0)} repo!"
                        sh 'exit 1' // failure due to not found forcedPR branch
                    }

                }
                // the first stage should always be the mainProject -> if it fails we can skip the rest!
                stage("gradle allTests ${projects.get(0)} with included builds") {

                    // display java version
                    sh "java -version"

                    // build and test the first project
                    log(i, "building and testing ${projects.get(0)}")
                    gradle("-p ${projects.get(0)} ${gradleTasks} ${mainProjectGradleTasks}")
                }


                stage('SonarQube analysis') {
                    withSonarQubeEnv() { // Will pick the global server connection from jenkins for sonarqube
                        gradle("-p ${projects.get(0)} sonarqube -Dsonar.projectKey=$sonarqubeProjectKey -Dsonar.pullrequest.branch=${featureBranchName} -Dsonar.pullrequest.key=${resolveBranchNo(env.BRANCH_NAME)} -Dsonar.pullrequest.base=master -Dsonar.pullrequest.github.repository=${orgNames.get(0)}/${projects.get(0)} -Dsonar.pullrequest.provider=Github")
                    }
                }

                stage("Quality Gate") {
                    timeout(time: 1, unit: 'HOURS') {
                        // Just in case something goes wrong, pipeline will be killed after a timeout
                        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }

                /**
                 * Post processing
                 * Publish reports and notify rocket chat
                 * Future clean workspace processes should be declared here
                 */
                stage('post processing') {
                    // publish reports
                    publishReports()

                    // notify rocket chat about success
                    String buildMode = "forcedPR"

                    rocketSend attachments: [
                            [$class: 'MessageAttachment', color: 'green', title: 'go to logs', titleLink: env.BUILD_URL],],
                            channel: rocketChatChannel,
                            message: ":jenkins_party: \n" +
                                    "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                    "${buildMode} successful!\n" +
                                    "*branch:* ${featureBranchName}"
                    rawMessage: true
                }

            } catch (Exception exc) {
                currentBuild.result = 'FAILURE'

                String buildMode = "forcedPR"
                String branchName = resolveBranchName(env.BRANCH_NAME, orgNames.get(0), projects.get(0))

                if (stageErrorMessage == 'Not a forcedPR. Abort!') {
                    buildMode = ""
                }

                println("[ERROR] [${date.format("dd/MM/yyyy")} - ${date.format("HH:mm:ss")}]" + exc)

                // notify rocketchat about failure
                rocketSend attachments: [
                        [$class: 'MessageAttachment', color: 'red', title: 'go to logs', titleLink: env.BUILD_URL],],
                        channel: rocketChatChannel,
                        message: ":jenkins_explode: \n" +
                                "${buildMode}  failed!\n" +
                                "*repo:* ${urls.get(0)}/${projects.get(0)}\n" +
                                "*branch:* ${branchName} \n" +
                                "*errorMessage:* ${stageErrorMessage}\n" +
                                "See logs for details.",
                        rawMessage: true

                // publish reports even on failure
                publishReports()

                // print the error message that might be thrown during one of the build stages
                error(stageErrorMessage)
            }

        }
    }

}

//////////////////////
// jenkins properties
/////////////////////
def getFeatureBranchProps(String prNo) {
    properties(
            [parameters(
                    [string(defaultValue: '', description: '', name: 'triggered', trim: true),
                     string(defaultValue: '', description: '', name: 'issue_title', trim: true),
                     string(defaultValue: '', description: '', name: 'comment_body', trim: true),
                     string(defaultValue: '', description: '', name: 'action', trim: true),
                     string(defaultValue: '', description: '', name: 'issue_url', trim: true),
                     string(defaultValue: '', description: '', name: 'sender_login', trim: true),
                     string(defaultValue: '', description: '', name: 'repository_name', trim: true)
                    ]),
             [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
             [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 0, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project'],
             [$class: 'JiraProjectProperty'],
             pipelineTriggers(
                     [GenericTrigger(causeString: '$issue_title',
                             genericVariables:
                                     [[defaultValue: '', key: 'comment_body', regexpFilter: '', value: '$.comment.body'],
                                      [defaultValue: '', key: 'action', regexpFilter: '', value: '$.action'],
                                      [defaultValue: '', key: 'issue_title', regexpFilter: '(WIP:\\s?)', value: '$.issue.title'],
                                      [defaultValue: '', key: 'issue_url', regexpFilter: '', value: '$.issue.url'],
                                      [defaultValue: '', key: 'sender_login', regexpFilter: '', value: '$.sender.login'],
                                      [defaultValue: '', key: 'repository_name', regexpFilter: '', value: '$.repository.name']
                                     ],
                             printContributedVariables: false,
                             printPostContent: false,
                             regexpFilterExpression: '^(!test created https:\\/\\/api\\.github\\.com\\/repos\\/.*\\/issues\\/' + prNo + ')$',
                             regexpFilterText: '$comment_body $action $issue_url',
                             silentResponse: true,
                             token: webhookTriggerToken)])])
}

def getMasterBranchProps() {
    properties(
            [parameters(
                    [string(defaultValue: '', description: '', name: 'triggered', trim: true),
                     string(defaultValue: '', description: '', name: 'action', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_title', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_merged', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_state', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_base_ref', trim: true),
                     string(defaultValue: '', description: '', name: 'pull_request_head_label', trim: true),
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
                                      [defaultValue: '', key: 'repository_name', regexpFilter: '', value: '$.repository.name']],

                             printContributedVariables: true,
                             printPostContent: true,
                             regexpFilterExpression: '^(closed true closed master)$',
                             regexpFilterText: '$action $pull_request_merged $pull_request_state $pull_request_base_ref',
                             silentResponse: true,
                             token: webhookTriggerToken)])])
}

// gradle wrapper for easy execution
// requires the gradle version to be configured with the same name under tools in jenkins configuration
def gradle(command) {
    env.JENKINS_NODE_COOKIE = 'dontKillMe' // this is necessary for the Gradle daemon to be kept alive
    sh "${tool name: 'gradle6.0.1', type: 'hudson.plugins.gradle.GradleInstallation'}/bin/gradle ${command}"
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

def resolveBranchNo(String featureBranchPRMinusNo) {
    // get pull request number
    def branchNoMatcher = featureBranchPRMinusNo =~ /PR-(.*)/
    assert branchNoMatcher.find()

    def prNo = branchNoMatcher[0][1]
    return prNo
}

def resolveBranchName(String featureBranchPRMinusNo, String orgName, String repoName) {

    // get pull request number
    def branchNoMatcher = featureBranchPRMinusNo =~ /PR-(.*)/
    assert branchNoMatcher.find()

    def prNo = branchNoMatcher[0][1]

    // curl the repo based on the feature branch no to get the branch information
    /// Note: only works for public repos! Otherwise credentials needs to be passed
    def curlUrl = "curl https://api.github.com/repos/" + orgName + "/" + repoName + "/pulls/" + prNo
    def response = curlUrl.execute().text
    def matcher = response =~ /\"label\":\s\"(.+)\"/

    assert matcher.find()

    // get split the label to account for PRs from forks
    def split = matcher[0][1] =~ /(.*):(.*)/

    assert matcher.find()

    def username = split[0][1]
    def branch = split[0][2]

    return branch

}

def isBranchIndexingCause() {
    def isBranchIndexing = false
    if (!currentBuild.rawBuild) {
        return true
    }

    currentBuild.rawBuild.getCauses().each { cause ->
        if (cause instanceof jenkins.branch.BranchIndexingCause) {
            isBranchIndexing = true
        }
    }
    return isBranchIndexing
}