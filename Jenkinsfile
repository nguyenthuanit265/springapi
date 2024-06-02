pipeline {
  // The following pipeline provides an opinionated template you can customize for your own needs.
  //
  // Instructions for configuring the Octopus plugin can be found at
  // https://octopus.com/docs/packaging-applications/build-servers/jenkins#configure-the-octopus-deploy-plugin
  //
  // Get a trial Octopus instance from https://octopus.com/start
  //
  // This pipeline requires the following plugins:
  // * Pipeline Utility Steps Plugin: https://wiki.jenkins.io/display/JENKINS/Pipeline+Utility+Steps+Plugin
  // * Git: https://plugins.jenkins.io/git/
  // * Workflow Aggregator: https://plugins.jenkins.io/workflow-aggregator/
  // * Octopus Deploy: https://plugins.jenkins.io/octopusdeploy/.
  // * JUnit: https://plugins.jenkins.io/junit/
  // * Maven Integration: https://plugins.jenkins.io/maven-plugin/
  parameters {
    // Parameters are only available after the first run. See https://issues.jenkins.io/browse/JENKINS-41929 for more details.
    string(defaultValue: 'Spaces-1', description: '', name: 'SpaceId', trim: true)
    string(defaultValue: 'springapi', description: '', name: 'ProjectName', trim: true)
    string(defaultValue: 'Dev', description: '', name: 'EnvironmentName', trim: true)
    string(defaultValue: 'Octopus', description: '', name: 'ServerId', trim: true)
  }
  tools {
    jdk 'Java'
  }
  agent 'any'
  stages {
    stage('Environment') {
      steps {
          echo "PATH = ${env.PATH}"
      }
    }
    stage('Checkout') {
      steps {
        // If this pipeline is saved as a Jenkinsfile in a git repo, the checkout stage can be deleted as
        // Jenkins will check out the code for you.
        script {
            /*
              This is from the Jenkins "Global Variable Reference" documentation:
              SCM-specific variables such as GIT_COMMIT are not automatically defined as environment variables; rather you can use the return value of the checkout step.
            */
            def checkoutVars = checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: 'https://github.com/nguyenthuanit265/springapi.git']]])
            env.GIT_URL = checkoutVars.GIT_URL
            env.GIT_COMMIT = checkoutVars.GIT_COMMIT
            env.GIT_BRANCH = checkoutVars.GIT_BRANCH
        }
      }
    }
    stage('Dependencies') {
      steps {
        // Download the dependencies and plugins before we attempt to do any further actions
        sh(script: './mvnw --batch-mode dependency:resolve-plugins dependency:go-offline')
        // Save the dependencies that went into this build into an artifact. This allows you to review any builds for vulnerabilities later on.
        sh(script: './mvnw --batch-mode dependency:tree > dependencies.txt')
        archiveArtifacts(artifacts: 'dependencies.txt', fingerprint: true)
        // List any dependency updates.
        sh(script: './mvnw --batch-mode versions:display-dependency-updates > dependencieupdates.txt')
        archiveArtifacts(artifacts: 'dependencieupdates.txt', fingerprint: true)
      }
    }
    stage('Build') {
      steps {
        // Set the build number on the generated artifact.
        sh '''
          ./mvnw --batch-mode build-helper:parse-version versions:set \
          -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.incrementalVersion}.${BUILD_NUMBER}
        '''
        sh(script: './mvnw --batch-mode -DskipTests clean compile', returnStdout: true)
        script {
            env.VERSION_SEMVER = sh (script: './mvnw -q -Dexec.executable=echo -Dexec.args=\'${project.version}\' --non-recursive exec:exec', returnStdout: true)
            env.VERSION_SEMVER = env.VERSION_SEMVER.trim()
        }
      }
    }
    stage('Test') {
      steps {
        sh(script: './mvnw --batch-mode test')
        junit(testResults: 'target/surefire-reports/*.xml', allowEmptyResults : true)
      }
    }
    stage('Package') {
      steps {
        sh(script: './mvnw --batch-mode package -DskipTests')
      }
    }
    stage('Repackage') {
      steps {
        // This scans through the build tool output directory and find the largest file, which we assume is the artifact that was intended to be deployed.
        // The path to this file is saved in and environment variable called JAVA_ARTIFACT, which can be consumed by subsequent custom deployment steps.
        script {
            // Find the matching artifacts
            def extensions = ['jar', 'war']
            def files = []
            for(extension in extensions){
                findFiles(glob: 'target/**.' + extension).each{files << it}
            }
            echo 'Found ' + files.size() + ' potential artifacts'
            // Assume the largest file is the artifact we intend to deploy
            def largestFile = null
            for (i = 0; i < files.size(); ++i) {
            	if (largestFile == null || files[i].length > largestFile.length) {
            		largestFile = files[i]
            	}
            }
            if (largestFile != null) {
            	env.ORIGINAL_ARTIFACT = largestFile.path
            	// Create a filename based on the repository name, the new version, and the original file extension.
            	env.ARTIFACTS = "springapi." + env.VERSION_SEMVER + largestFile.path.substring(largestFile.path.lastIndexOf("."), largestFile.path.length())
            	echo 'Found artifact at ' + largestFile.path
            	echo 'This path is available from the ARTIFACTS environment variable.'
            }
        }
        // Octopus requires files to have a specific naming format. So copy the original artifact into a file with the correct name.
        sh(script: 'cp ${ORIGINAL_ARTIFACT} ${ARTIFACTS}')
      }
    }
    stage('Deployment') {
      steps {
        // This stage assumes you perform the deployment with Octopus Deploy.
        // The steps shown below can be replaced with your own custom steps to deploy to other platforms if needed.
        octopusPushPackage(additionalArgs: '',
          packagePaths: env.ARTIFACTS.split(":").join("\n"),
          overwriteMode: 'OverwriteExisting',
          serverId: params.ServerId,
          spaceId: params.SpaceId,
          toolId: 'Default')
        octopusPushBuildInformation(additionalArgs: '',
          commentParser: 'GitHub',
          overwriteMode: 'OverwriteExisting',
          packageId: env.ARTIFACTS.split(":")[0].substring(env.ARTIFACTS.split(":")[0].lastIndexOf("/") + 1, env.ARTIFACTS.split(":")[0].length()).replaceAll("\\." + env.VERSION_SEMVER + "\\..+", ""),
          packageVersion: env.VERSION_SEMVER,
          serverId: params.ServerId,
          spaceId: params.SpaceId,
          toolId: 'Default',
          verboseLogging: false,
          gitUrl: env.GIT_URL,
          gitCommit: env.GIT_COMMIT,
          gitBranch: env.GIT_BRANCH)
        octopusCreateRelease(additionalArgs: '',
          cancelOnTimeout: false,
          channel: '',
          defaultPackageVersion: '',
          deployThisRelease: false,
          deploymentTimeout: '',
          environment: params.EnvironmentName,
          jenkinsUrlLinkback: false,
          project: params.ProjectName,
          releaseNotes: false,
          releaseNotesFile: '',
          releaseVersion: env.VERSION_SEMVER,
          serverId: params.ServerId,
          spaceId: params.SpaceId,
          tenant: '',
          tenantTag: '',
          toolId: 'Default',
          verboseLogging: false,
          waitForDeployment: false)
        octopusDeployRelease(cancelOnTimeout: false,
          deploymentTimeout: '',
          environment: params.EnvironmentName,
          project: params.ProjectName,
          releaseVersion: env.VERSION_SEMVER,
          serverId: params.ServerId,
          spaceId: params.SpaceId,
          tenant: '',
          tenantTag: '',
          toolId: 'Default',
          variables: '',
          verboseLogging: false,
          waitForDeployment: true)
      }
    }
  }
}