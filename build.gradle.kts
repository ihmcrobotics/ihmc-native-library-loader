plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.ihmc-ci") version "8.0"
   id("us.ihmc.ihmc-cd") version "1.24"
}

ihmc {
   group = "us.ihmc"
   version = "2.0.2"
   vcsUrl = "https://github.com/ihmcrobotics/ihmc-native-library-loader"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("org.apache.commons:commons-lang3:3.12.0")
   api("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
}
