plugins {
   id("us.ihmc.ihmc-build")
}

ihmc {
   group = "us.ihmc"
   version = "2.0.5"
   vcsUrl = "https://github.com/ihmcrobotics/ihmc-native-library-loader"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

tasks.jar {
   manifest {
      attributes("Automatic-Module-Name" to "us.ihmc.nativelibraryloader")
   }
}
