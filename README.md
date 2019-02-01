# zlcore
zaitunlabs's android library core

There are a lot of utility that you can use from this library :
1. Form Builder
2. Form Validation
3. Android Permission Handler
4. Splash Page
5. Login Page
6. OnBoarding Page
7. Version Change History Page
8. Push Notification Handler With/Without Notification List Page
9. Webview Page
10. Percentage Page
11. RecyclerView that support load more, swype, drag
12. About Page
13. Data Intent Service
14. SMS Listener
15. View Binding Utility
16. Table View Utility
17. Date String Utility
18. Show Dialog/SnackBar Utility
19. Show Date/Time/Contact/File/Place Picker
20. and many more in package folder 'com.zaitunlabs.zlcore.utils' especially 'CommonUtils' static class






How to use this library:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.ahsai001:zlcore:1.0.3.3' //use this for non-androidx version
		//or
		implementation 'com.github.ahsai001:zlcore:1.1.18.20' //use this for androidx version
	}
