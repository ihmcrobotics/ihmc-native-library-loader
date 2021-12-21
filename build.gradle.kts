plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.ihmc-ci") version "7.5"
   id("us.ihmc.ihmc-cd") version "1.22"
}

ihmc {
   group = "us.ihmc"
   version = "1.3.1"
   vcsUrl = "https://github.com/ihmcrobotics/ihmc-native-library-loader"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("org.apache.commons:commons-lang3:3.12.0")
   api("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
}
