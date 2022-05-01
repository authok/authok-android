# authok-android

[![CircleCI](https://img.shields.io/circleci/project/github/authok/authok-android.svg?style=flat-square)](https://circleci.com/gh/authok/authok-android/tree/master)
[![Coverage Status](https://img.shields.io/codecov/c/github/authok/authok-android/master.svg?style=flat-square)](https://codecov.io/github/authok/authok-android)
[![License](https://img.shields.io/:license-mit-blue.svg?style=flat-square)](https://doge.mit-license.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.authok.android/authok.svg?style=flat-square)](https://search.maven.org/artifact/cn.authok.android/authok)
[![javadoc](https://javadoc.io/badge2/cn.authok.android/authok/javadoc.svg)](https://javadoc.io/doc/cn.authok.android/authok)

Android Java & Kotlin toolkit for consuming the Authok Authentication API

## Requirements

Android API version 21 or later and Java 8+.

Here’s what you need in `build.gradle` to target Java 8 byte code for Android and Kotlin plugins respectively.

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}
```

## 安装

### Gradle

authok-android is available through [Gradle](https://gradle.org/). 在 `build.gradle` 文件中添加如下代码:

```gradle
dependencies {
    implementation 'cn.authok.android:authok:2.7.0'
}
```

### 权限

打开应用的 `AndroidManifest.xml` 文件并添加如下权限.

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 使用

首先, 创建 `Authok` 实例

```kotlin
val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}")
```

可以把配置保存在 `strings.xml` 文件:

```xml
<resources>
    <string name="cn_authok_client_id">YOUR_CLIENT_ID</string>
    <string name="cn_authok_domain">YOUR_DOMAIN</string>
</resources>

```

可以通过 Android Context 来创建 Authok 实例:

```kotlin
val account = Authok(context)
```

### OIDC 兼容模式

Beginning in version 2, this SDK is OIDC-Conformant by default, and will not use any legacy authentication endpoints.

更多信息可参考 [OIDC adoption guide](https://docs.authok.cn/docs/api-auth/tutorials/adoption).

### 使用统一登录进行认证

首先进入 [Authok 管理后台](https://mgmt.authok.cn/app/applications) 并进入到应用设置页面. 确保 **回调 URL** 包含如下 URL:

```
https://{YOUR_AUTHOK_DOMAIN}/android/{YOUR_APP_PACKAGE_NAME}/callback
```

用应用的包名替换 `{YOUR_APP_PACKAGE_NAME}`, 应用包名为 `app/build.gradle` 文件的 `applicationId`.

接下来, 为 Authok 域名 和 Scheme 定义 Manifest 占位符, 内部将注册一个 **intent-filter**. 打开 `build.gradle` 文件并给 `manifestPlaceholders` 添加如下行:

```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 30
    defaultConfig {
        applicationId "cn.authok.samples"
        minSdkVersion 21
        targetSdkVersion 30
        //...

        //---> Add the next line
        manifestPlaceholders = [authokDomain: "@string/cn_authok_domain", authokScheme: "https"]
        //<---
    }
    //...
}
```

比较好的做法是定义一个可重用资源 `@string/cn_authok_domain`. scheme 可以为 `https` 或者任何自定义值. 可以阅读 [此章节](#a-note-about-app-deep-linking) 来了解更多.

添加互联网权限.

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

声明用于接收身份验证结果的回调实例.

```kotlin
val callback = object : Callback<Credentials, AuthenticationException> {
    override fun onFailure(exception: AuthenticationException) {
        // 失败! 检查异常细节
    }

    override fun onSuccess(credentials: Credentials) {
        // 成功! 可以使用 Access token 和 ID token
    }
}
```

最后, 可以展示 **Authok 统一登录** 进行认证:

```kotlin
// 配置并启动认证
WebAuthProvider.login(account)
    .start(this, callback)
```

当用户返回应用程序时，将调用回调。在以下情况可能会失败:
* 由于未安装任何兼容的浏览器应用程序，设备无法打开URL. 你可以检查 `error.isBrowserAppNotAvailable` 错误是否存在.
* 当用户手动关闭浏览器时（例如按后退键. 你可以检查 `error.isAuthenticationCanceled` 错误是否存在.
* 当服务器出现错误时. 检查接收到的异常详情.

如果 `redirect` URL 不在应用的 **回调 URL** 中, 服务器将不执行重定向，浏览器将保持打开状态.

#### 令牌验证
The ID token received as part of this web authentication flow is automatically verified following the [OpenID Connect specification](https://openid.net/specs/openid-connect-core-1_0.html).

If you are a user of Authok Private Cloud with ["Custom Domains"](https://docs.authok.cn/docs/custom-domains) still on the [legacy behavior](https://docs.authok.cn/docs/private-cloud/private-cloud-migrations/migrate-private-cloud-custom-domains#background), you need to override the expected issuer to match your Authok domain before starting the authentication.

```kotlin
val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_CUSTOM_DOMAIN}")

WebAuthProvider.login(account)
    .withIdTokenVerificationIssuer("https://{YOUR_AUTHOK_DOMAIN}/")
    .start(this, callback)
```

##### A note about App Deep Linking:

If you followed the configuration steps documented here, you may have noticed the default scheme used for the Callback URI is `https`. This works best for Android API 23 or newer if you're using [Android App Links](https://docs.authok.cn/docs/applications/enable-android-app-links), but in previous Android versions this _may_ show the intent chooser dialog prompting the user to choose either your application or the browser. You can change this behaviour by using a custom unique scheme so that the OS opens directly the link with your app. Note that the schemes [can only have lowercase letters](https://developer.android.com/guide/topics/manifest/data-element).

1. Update the `authokScheme` Manifest Placeholder on the `app/build.gradle` file or update the intent-filter declaration in the `AndroidManifest.xml` to use the new scheme.
2. Update the **Allowed Callback URLs** in your [Authok 管理后台](https://mgmt.authok.cn/app/applications) application's settings.
3. Call `withScheme()` in the `WebAuthProvider` builder passing the custom scheme you want to use.


```kotlin
WebAuthProvider.login(account)
    .withScheme("myapp")
    .start(this, callback)
```


#### Authenticate with any Authok connection

The connection must first be enabled in the Authok dashboard for this Authok application.

```kotlin
WebAuthProvider.login(account)
    .withConnection("twitter")
    .start(this, callback)
```

#### Specify audience

```kotlin
WebAuthProvider.login(account)
    .withAudience("https://{YOUR_AUTHOK_DOMAIN}/api/v1/")
    .start(this, callback)
```

上面的例子通过 audience 来请求 令牌，需要调用 [Management API](https://docs.authok.cn/docs/api/management/v1) 端点.
 
> 用你的真实 Authok域名(例如: `mytenant.cn.authok.cn`) 来替换 `{YOUR_AUTHOK_DOMAIN}`. 如果您配置了 "自定义域名", 也可以使用.

#### 指定 scope

```kotlin
WebAuthProvider.login(account)
    .withScope("openid profile email read:users")
    .start(this, callback)
```

> 默认 scope 为 `openid profile email`. 无论是否有指定, `openid` scope 将会强制设置.

#### 指定身份源 scope

```kotlin
WebAuthProvider.login(account)
    .withConnectionScope("email", "profile", "calendar:read")
    .start(this, callback)
```

#### 自定义 Custom Tabs UI

如果运行应用程序的设备具有自定义选项卡兼容浏览器, 注销流程将首选自定义选项卡. 您可以使用 `CustomTabsOptions` 类自定义页面标题是否可见、工具栏颜色以及受支持的浏览器应用程序.
 
```kotlin
val ctOptions = CustomTabsOptions.newBuilder()
    .withToolbarColor(R.color.ct_toolbar_color)
    .showTitle(true)
    .build()
 
WebAuthProvider.login(account)
    .withCustomTabsOptions(ctOptions)
    .start(this, callback)
```


### 清除会话

To log the user out and clear the SSO cookies that the Authok Server keeps attached to your browser app, you need to call the [退登端点](https://docs.authok.cn/docs/api/authentication?#logout). This can be done is a similar fashion to how you authenticated before: using the `WebAuthProvider` class.

Make sure to [revisit that section](#authentication-with-universal-login) to configure the Manifest Placeholders if you still cannot authenticate successfully. The values set there are used to generate the URL that the server will redirect the user back to after a successful log out.

In order for this redirection to happen, you must copy the **回调 URL** value you added for authentication into the **Allowed Logout URLs** field in your [application settings](https://mgmt.authok.cn/app/applications). Both fields should have an URL with the following format:


```
https://{YOUR_AUTHOK_DOMAIN}/android/{YOUR_APP_PACKAGE_NAME}/callback
```

Remember to replace `{YOUR_APP_PACKAGE_NAME}` with your actual application's package name, available in your `app/build.gradle` file as the `applicationId` value.


Initialize the provider, this time calling the static method `logout`.

```kotlin
//Configure and launch the log out
WebAuthProvider.logout(account)
    .start(this, logoutCallback)

//Declare the callback that will receive the result
val logoutCallback = object: Callback<Void?, AuthenticationException> {
    override fun onFailure(exception: AuthenticationException) {
        // Failure! Check the exception for details
    }

    override fun onSuccess(result: Void?) {
        // Success! The browser session was cleared
    }
}
```

The callback will get invoked when the user returns to your application. There are a few scenarios where this may fail:

* When the device cannot open the URL because it doesn't have any compatible browser application installed. You can check this scenario with `error.isBrowserAppNotAvailable`.
* When the user manually closed the browser (e.g. pressing the back key). You can check this scenario with `error.isAuthenticationCanceled`.

If the `returnTo` URL is not found in the **Allowed Logout URLs** of your Authok Application, the server will not make the redirection and the browser will remain open.

#### Changing the Return To URL scheme
This configuration will probably match what you've done for the [authentication setup](#a-note-about-app-deep-linking).

```kotlin
WebAuthProvider.logout(account)
    .withScheme("myapp")
    .start(this, logoutCallback)
```

#### Customize the Custom Tabs UI

If the device where the app is running has a Custom Tabs compatible Browser, a Custom Tab will be preferred for the logout flow. You can customize the Page Title visibility, the Toolbar color, and the supported Browser applications by using the `CustomTabsOptions` class.
 
```kotlin
val ctOptions = CustomTabsOptions.newBuilder()
    .withToolbarColor(R.color.ct_toolbar_color)
    .showTitle(true)
    .build()
 
WebAuthProvider.logout(account)
    .withCustomTabsOptions(ctOptions)
    .start(this, logoutCallback)
```

## Next steps

### Learning resources

Check out the [Android QuickStart Guide](https://docs.authok.cn/docs/quickstart/native/android) to find out more about the authok-android toolkit and explore our tutorials and sample projects.

### Authentication API

The client provides methods to authenticate the user against the Authok server.

Create a new instance by passing the account:

```kotlin
val authentication = AuthenticationAPIClient(account)
```

**Note:** If your Authok account has the ["Bot Protection"](https://docs.authok.cn/docs/anomaly-detection/bot-protection) feature enabled, your requests might be flagged for verification. Read how to handle this scenario on the [Bot Protection](#bot-protection) section.

#### Login with database connection

```kotlin
authentication
    .login("a@authok.cn", "密码", "my-database-connection")
    .start(object: Callback<Credentials, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) { }

        override fun onSuccess(credentials: Credentials) { }
    })
```

> The default scope used is `openid profile email`. Regardless of the scopes set to the request, the `openid` scope is always enforced.


#### Login using MFA with One Time Password code

This call requires the client to have the *MFA* Client Grant Type enabled. Check [this article](https://docs.authok.cn/docs/clients/client-grant-types) to learn how to enable it.

When you sign in to a multifactor authentication enabled connection using the `login` method, you receive an error standing that MFA is required for that user along with an `mfa_token` value. Use this value to call `loginWithOTP` and complete the MFA flow passing the One Time Password from the enrolled MFA code generator app.

```kotlin
authentication
    .loginWithOTP("the mfa token", "123456")
    .start(object: Callback<Credentials, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) { }

        override fun onSuccess(credentials: Credentials) { }
    })
```

> The default scope used is `openid profile email`. Regardless of the scopes set to the request, the `openid` scope is always enforced.


#### Passwordless Login

This feature requires your Application to have the *Passwordless OTP* enabled. See [this article](https://docs.authok.cn/docs/clients/client-grant-types) to learn how to enable it.

Passwordless it's a 2 steps flow:

Step 1: Request the code

```kotlin
authentication
    .passwordlessWithEmail("info@authok.cn", PasswordlessType.CODE, "my-passwordless-connection")
    .start(object: Callback<Void, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) { }

        override fun onSuccess(result: Void?) { }
    })
```


Step 2: Input the code

```kotlin
authentication
    .loginWithEmail("info@authok.cn", "123456", "my-passwordless-connection")
    .start(object: Callback<Credentials, AuthenticationException> {
       override fun onFailure(exception: AuthenticationException) { }

       override fun onSuccess(credentials: Credentials) { }
   })
```

> The default scope used is `openid profile email`. Regardless of the scopes set to the request, the `openid` scope is always enforced.

#### Sign Up with database connection

```kotlin
authentication
    .signUp("info@authok.cn", "a secret password", "my-database-connection")
    .start(object: Callback<Credentials, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) { }

        override fun onSuccess(credentials: Credentials) { }
    })
```

> The default scope used is `openid profile email`. Regardless of the scopes set to the request, the `openid` scope is always enforced.

#### Get user information

```kotlin
authentication
   .userInfo("user access_token")
   .start(object: Callback<UserProfile, AuthenticationException> {
       override fun onFailure(exception: AuthenticationException) { }

       override fun onSuccess(profile: UserProfile) { }
   })
```


#### Bot Protection
If you are using the [Bot Protection](https://docs.authok.cn/docs/anomaly-detection/bot-protection) feature and performing database login/signup via the Authentication API, you need to handle the `AuthenticationException#isVerificationRequired()` error. It indicates that the request was flagged as suspicious and an additional verification step is necessary to log the user in. That verification step is web-based, so you need to use Universal Login to complete it.

```kotlin
val email = "info@authok.cn"
val password = "a secret password"
val realm = "my-database-connection"

val authentication = AuthenticationAPIClient(account)
authentication.login(email, password, realm)
    .start(object: Callback<Credentials, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) {
            if (exception.isVerificationRequired()) {
                val params = mapOf("login_hint" to email) // So the user doesn't have to type it again
                WebAuthProvider.login(account)
                    .withConnection(realm)
                    .withParameters(params)
                    .start(LoginActivity.this, object: Callback<Credentials, AuthenticationException> {
                        // You might already have a Callback instance defined

                        override fun onFailure(exception: AuthenticationException) {
                            // Handle error
                        }

                        override fun onSuccess(credentials: Credentials) {
                            // Handle WebAuth success
                        }
                    })
            }
            // Handle other errors
        }

        override fun onSuccess(credentials: Credentials) {
            // Handle API success
        }
    })
```

In the case of signup, you can add [an additional parameter](https://docs.authok.cn/docs/universal-login/new-experience#signup) to make the user land directly on the signup page:

```kotlin
val params = mapOf(
    "login_hint" to email, 
    "screen_hint", "signup"
)
```

Check out how to set up Universal Login in the [Authentication with Universal Login](#authentication-with-universal-login) section.

### Management API

The client provides a few methods to interact with the [Users Management API](https://docs.authok.cn/docs/api/management/v1/#!/Users).

Create a new instance passing the account and an access token with the Management API audience and the right scope:

```kotlin
val users = UsersAPIClient(account, "api access token")
```

#### Link users

```kotlin
users
    .link("primary user id", "secondary user token")
    .start(object: Callback<List<UserIdentity>, ManagementException> {
    
        override fun onFailure(exception: ManagementException) { }
    
        override fun onSuccess(identities: List<UserIdentity>) { }
    })
```

#### Unlink users

```kotlin
users
    .unlink("primary user id", "secondary user id", "secondary provider")
    .start(object: Callback<List<UserIdentity>, ManagementException> {
    
        override fun onFailure(exception: ManagementException) { }
    
        override fun onSuccess(identities: List<UserIdentity>) { }
    })
```

#### Get User Profile

```kotlin
users
    .getProfile("user id")
    .start(object: Callback<UserProfile, ManagementException> {
    
        override fun onFailure(exception: ManagementException) { }
    
        override fun onSuccess(identities: UserProfile) { }
    })
```

#### Update User Metadata

```kotlin
val metadata = mapOf(
    "name" to listOf("My", "Name", "Is"),
    "phoneNumber" to "1234567890"
)

users
    .updateMetadata("user id", metadata)
    .start(object: Callback<UserProfile, ManagementException> {
    
        override fun onFailure(exception: ManagementException) { }
    
        override fun onSuccess(identities: UserProfile) { }
    })
```

> In all the cases, the `user ID` parameter is the unique identifier of the authok account instance. i.e. in `google-oauth2|123456789` it would be the part after the '|' pipe: `123456789`.

### Organizations

[Organizations](https://docs.authok.cn/docs/organizations) is a set of features that provide better support for developers who build and maintain SaaS and Business-to-Business (B2B) applications. 

Using Organizations, you can:

- Represent teams, business customers, partner companies, or any logical grouping of users that should have different ways of accessing your applications, as organizations.
- Manage their membership in a variety of ways, including user invitation.
- Configure branded, federated login flows for each organization.
- Implement role-based access control, such that users can have different roles when authenticating in the context of different organizations.
- Build administration capabilities into your products, using Organizations APIs, so that those businesses can manage their own organizations.

Note that Organizations is currently only available to customers on our Enterprise and Startup subscription plans.

#### Log in to an organization

```kotlin
WebAuthProvider.login(account)
    .withOrganization(organizationId)
    .start(this, callback)
```

#### Accept user invitations

Users can be invited to your organization via a link. Tapping on the invitation link should open your app. Since invitations links are `https` only, is recommended that your app supports [Android App Links](https://developer.android.com/training/app-links). In [Enable Android App Links Support](https://docs.authok.cn/docs/applications/enable-android-app-links-support), you will find how to make the Authok server publish the Digital Asset Links file required by your application.

When your app gets opened by an invitation link, grab the invitation URL from the received Intent (e.g. in `onCreate` or `onNewIntent`) and pass it to `.withInvitationUrl()`:

```kotlin
getIntent()?.data?.let {
    WebAuthProvider.login(account)
        .withInvitationUrl(invitationUrl)
        .start(this, callback)
}
```

If the URL doesn't contain the expected values, an error will be raised through the provided callback.

## Credentials Manager

This library ships with two additional classes that help you manage the Credentials received during authentication.

### Basic

The basic version supports asking for `Credentials` existence, storing them and getting them back. If the credentials have expired and a refresh_token was saved, they are automatically refreshed. The class is called `CredentialsManager`.

#### Usage
1. **Instantiate the manager:**
You'll need an `AuthenticationAPIClient` instance to renew the credentials when they expire and a `Storage` object. We provide a `SharedPreferencesStorage` class that makes use of `SharedPreferences` to create a file in the application's directory with **Context.MODE_PRIVATE** mode.

```kotlin
val authentication = AuthenticationAPIClient(account)
val storage = SharedPreferencesStorage(this)
val manager = CredentialsManager(authentication, storage)
```

2. **Save credentials:**
The credentials to save **must have** `expires_at` and at least an `access_token` or `id_token` value. If one of the values is missing when trying to set the credentials, the method will throw a `CredentialsManagerException`. If you want the manager to successfully renew the credentials when expired you must also request the `offline_access` scope when logging in in order to receive a `refresh_token` value along with the rest of the tokens. i.e. Logging in with a database connection and saving the credentials:

```kotlin
authentication
    .login("info@authok.cn", "a secret password", "my-database-connection")
    .setScope("openid email profile offline_access")
    .start(object : Callback<Credentials, AuthenticationException> {
        override fun onFailure(exception: AuthenticationException) {
            // Error
        }
    
        override fun onSuccess(credentials: Credentials) {
            //Save the credentials
            manager.saveCredentials(credentials)
        }
    })
``` 
**Note:** This method has been made thread-safe after version 2.7.0.

3. **Check credentials existence:**
There are cases were you just want to check if a user session is still valid (i.e. to know if you should present the login screen or the main screen). For convenience, we include a `hasValidCredentials` method that can let you know in advance if a non-expired token is available without making an additional network call. The same rules of the `getCredentials` method apply:

```kotlin
val authenticated = manager.hasValidCredentials()
```

4. **Retrieve credentials:**
Existing credentials will be returned if they are still valid, otherwise the `refresh_token` will be used to attempt to renew them. If the `expires_at` or both the `access_token` and `id_token` values are missing, the method will throw a `CredentialsManagerException`. The same will happen if the credentials have expired and there's no `refresh_token` available.

```kotlin
manager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
   override fun onFailure(exception: CredentialsManagerException) {
       // Error
   }

   override fun onSuccess(credentials: Credentials) {
       // Use the credentials
   }
})
``` 
**Note:** In the scenario where the stored credentials have expired and a `refresh_token` is available, the newly obtained tokens are automatically saved for you by the Credentials Manager. This method has been made thread-safe after version 2.7.0.

5. **Clear credentials:**
When you want to log the user out:

```kotlin
manager.clearCredentials()
```


### Encryption enforced

This version adds encryption to the data storage. Additionally, in those devices where a Secure Lock Screen has been configured it can require the user to authenticate before letting them obtain the stored credentials. The class is called `SecureCredentialsManager`.


#### Usage
The usage is similar to the previous version, with the slight difference that the manager now requires a valid android `Context` as shown below:

```kotlin
val authentication = AuthenticationAPIClient(account)
val storage = SharedPreferencesStorage(this)
val manager = SecureCredentialsManager(this, authentication, storage)
```

#### Requiring Authentication

You can require the user authentication to obtain credentials. This will make the manager prompt the user with the device's configured Lock Screen, which they must pass correctly in order to obtain the credentials. **This feature is only available on devices where the user has setup a secured Lock Screen** (PIN, Pattern, Password or Fingerprint).

To enable authentication you must call the `requireAuthentication` method passing a valid _Activity_ context, a request code that represents the authentication call, and the title and description to display in the Lock Screen. As seen in the snippet below, you can leave these last two parameters with `null` to use the system's default title and description. It's only safe to call this method before the Activity is started. 

```kotlin
//You might want to define a constant with the Request Code
companion object {
    const val AUTH_REQ_CODE = 111
}

manager.requireAuthentication(this, AUTH_REQ_CODE, null, null)
```

When the above conditions are met and the manager requires the user authentication, it will use the activity context to launch the Lock Screen activity and wait for its result. If your activity is a subclass of `ComponentActivity`, this will be handled automatically for you internally. Otherwise, your activity must override the `onActivityResult` method and pass the request code and result code to the manager's `checkAuthenticationResult` method to verify if this request was successful or not.

```kotlin
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (manager.checkAuthenticationResult(requestCode, resultCode)) {
        return
    }
    super.onActivityResult(requestCode, resultCode, data)
}
```

If the manager consumed the event, it will return true and later invoke the callback's `onSuccess` with the decrypted credentials.


#### Handling exceptions

In the event that something happened while trying to save or retrieve the credentials, a `CredentialsManagerException` will be thrown. These are some of the expected failure scenarios:

- Invalid Credentials format or values. e.g. when it's missing the `access_token`, the `id_token` or the `expires_at` values.
- Tokens have expired but no `refresh_token` is available to perform a refresh credentials request.
- Device's Lock Screen security settings have changed (e.g. the PIN code was changed). Even when `hasCredentials` returns true, the encryption keys will be deemed invalid and until `saveCredentials` is called again it won't be possible to decrypt any previously existing content, since they keys used back then are not the same as the new ones.
- Device is not compatible with some of the algorithms required by the `SecureCredentialsManager` class. This is considered a catastrophic event and might happen when the OEM has modified the Android ROM removing some of the officially included algorithms. Nevertheless, it can be checked in the exception instance itself by calling `isDeviceIncompatible`. By doing so you can decide the fallback for storing the credentials, such as using the regular `CredentialsManager`.

## Networking client customization

This library provides the ability to customize the behavior of the networking client for common configurations, as well the ability to define and use your own networking client implementation.

The Authok class can be configured with a `NetworkingClient`, which will be used when making requests. You can configure the default client with custom timeout values, any headers that should be sent on all requests, and whether to log request/response info (for non-production debugging purposes only). For more advanced configuration, you can provide your own implementation of `NetworkingClient`.

### Timeout configuration

```kotlin
val netClient = DefaultClient(
    connectTimeout = 30,
    readTimeout = 30
)

val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}")
account.networkingClient = netClient
```

### Logging configuration

```kotlin
val netClient = DefaultClient(
    enableLogging = true
)

val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}")
account.networkingClient = netClient
```

### Set additional headers for all requests

```kotlin
val netClient = DefaultClient(
    defaultHeaders = mapOf("{HEADER-NAME}" to "{HEADER-VALUE}")
)

val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}")
account.networkingClient = netClient
```

### Advanced configuration

For more advanced configuration of the networking client, you can provide a custom implementation of `NetworkingClient`. This may be useful when you wish to reuse your own networking client, configure a proxy, etc.

```kotlin
class CustomNetClient : NetworkingClient {
    override fun load(url: String, options: RequestOptions): ServerResponse {
        // Create and execute the request to the specified URL with the given options
        val response = // ...
            
        // Return a ServerResponse from the received response data
        return ServerResponse(responseCode, responseBody, responseHeaders)        
    }
}

val account = Authok("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}")
account.networkingClient = netClient
```

## FAQ

### Why is the Android Lint _error_ `'InvalidPackage'` considered a _warning_?

When building the project with `build`, an error appeared regarding an `invalid package` on the `okio` dependency. This snippet is in the `build.gradle` file so that the build runs fine:

```gradle
android {
    //...
    lintOptions {
       warning 'InvalidPackage'
    }
}
```

ref: https://github.com/square/okio/issues/58#issuecomment-72672263

### Why do I need to declare Manifest Placeholders for the Authok domain and scheme?

The library internally declares a `RedirectActivity` in its Android Manifest file. While this approach prevents the developer from adding an activity declaration to their application's Android Manifest file, it requires the use of Manifest Placeholders.

Alternatively, you can re-declare the `RedirectActivity` in the `AndroidManifest.xml` file with your own **intent-filter** so it overrides the library's default. If you do this then the `manifestPlaceholders` don't need to be set as long as the activity contains the `tools:node="replace"` like in the snippet below.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="your.app.package">
    <application android:theme="@style/AppTheme">

        <!-- ... -->

        <activity
            android:name="cn.authok.android.provider.RedirectActivity"
            tools:node="replace">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data
                    android:host="@string/cn_authok_domain"
                    android:pathPrefix="/android/${applicationId}/callback"
                    android:scheme="https" />
            </intent-filter>
        </activity>

        <!-- ... -->

    </application>
</manifest>
```

Recall that if you request a different scheme, you must replace the above `android:scheme` property value and initialize the provider with the new scheme. Read [this section](#a-note-about-app-deep-linking) to learn more. 


### Is the Web Authentication module setup optional?

If you don't plan to use the _Web Authentication_ feature, you will notice that the compiler will still prompt you to provide the `manifestPlaceholders` values, since the `RedirectActivity` included in this library will require them, and the Gradle tasks won't be able to run without them. 

Re-declare the activity manually with `tools:node="remove"` in your app's Android Manifest in order to make the manifest merger remove it from the final manifest file. Additionally, one more unused activity can be removed from the final APK by using the same process. A complete snippet to achieve this is:

```xml
<activity
    android:name="cn.authok.android.provider.AuthenticationActivity"
    tools:node="remove"/>
<!-- Optional: Remove RedirectActivity -->
<activity
    android:name="cn.authok.android.provider.RedirectActivity"
    tools:node="remove"/>
```

### Unit testing with JUnit 4 or JUnit 5
#### Handling `Method getMainLooper in android.os.Looper not mocked` errors
Your unit tests might break with `Caused by: java.lang.RuntimeException: Method getMainLooper in android.os.Looper not mocked` due to the Looper being used internally by this library. There are two options to handle this:
1. Use Robolectric Shadows - see this [test](https://github.com/authok/authok-android/blob/main/authok/src/test/java/com/authok/android/authentication/AuthenticationAPIClientTest.kt#L44-L45) for an example
2. If your project does not use Robolectric and uses JUnit 4, you can create a `Rule` that you can add to your unit test:
```kotlin
import cn.authok.android.request.internal.CommonThreadSwitcher
import cn.authok.android.request.internal.ThreadSwitcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description

public class CommonThreadSwitcherRule : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        CommonThreadSwitcher.getInstance().setDelegate(object : ThreadSwitcher {
            override fun mainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun backgroundThread(runnable: Runnable) {
                runnable.run()
            }
        })
    }

    override fun finished(description: Description) {
        super.finished(description)
        CommonThreadSwitcher.getInstance().setDelegate(null)
    }
}
```
See this [test](https://github.com/authok/authok-android/blob/main/authok/src/test/java/com/authok/android/request/internal/CommonThreadSwitcherDelegateTest.kt) for an example of it being used.

3. If you use JUnit 5 then you can create an `Extension` similar to the previous `Rule` for JUnit 4:
```kotlin
import cn.authok.android.request.internal.CommonThreadSwitcher
import cn.authok.android.request.internal.ThreadSwitcher
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CommonThreadSwitcherExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        CommonThreadSwitcher.getInstance().setDelegate(object : ThreadSwitcher {
            override fun mainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun backgroundThread(runnable: Runnable) {
                runnable.run()
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        CommonThreadSwitcher.getInstance().setDelegate(null)
    }

}
```

#### Handling SSL errors
You might encounter errors similar to `PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target`, which means that you need to set up your unit tests in a way that ignores or trusts all SSL certificates. In that case, you may have to implement your own `NetworkingClient` so that you can supply your own `SSLSocketFactory` and `X509TrustManager`, and use that in creating your `Authok` object. See the [`DefaultClient`](https://github.com/authok/authok-android/blob/main/authok/src/main/java/cn/authok/android/request/DefaultClient.kt) class for an idea on how to extend `NetworkingClient`.

## Proguard
The rules should be applied automatically if your application is using `minifyEnabled = true`. If you want to include them manually check the [proguard directory](proguard).
By default you should at least use the following files:
* `proguard-okio.pro`
* `proguard-gson.pro`

## What is Authok?

Authok helps you to:

* Add authentication with [multiple authentication sources](https://docs.authok.cn/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, among others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.authok.cn/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.authok.cn/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.authok.cn/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.authok.cn/rules).

## 创建一个免费的 Authok 账号

1. 进入 [Authok](https://authok.cn) 并点击注册.
2. 使用 微信，企业微信, Google 或 GitHub 进行登录.

## 问题报告

如果您发现bug或有一个功能需求，请给仓库提交Issuer。请不要在公共GitHub问题跟踪器上报告安全漏洞。

## 作者

[Authok](https://authok.cn)

## 许可

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.
