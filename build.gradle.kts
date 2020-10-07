plugins {
   id("us.ihmc.ihmc-build") version "0.22.0"
   id("us.ihmc.ihmc-ci") version "6.4"
   id("us.ihmc.ihmc-cd") version "1.8"
}

ihmc {
   group = "us.ihmc"
   version = "1.3.0"
   vcsUrl = "https://github.com/ihmcrobotics/ihmc-native-library-loader"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("org.apache.commons:commons-lang3:3.9")
   api("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
}
