package cn.authok.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.authok.android.Authok
import cn.authok.android.authentication.AuthenticationAPIClient
import cn.authok.android.authentication.AuthenticationException
import cn.authok.android.callback.Callback
import cn.authok.android.provider.WebAuthProvider
import cn.authok.android.request.DefaultClient
import cn.authok.android.result.Credentials
import cn.authok.sample.databinding.FragmentDatabaseLoginBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DatabaseLoginFragment : Fragment() {

    private val account: Authok by lazy {
        // -- REPLACE this credentials with your own Authok app credentials!
        val account = Authok(
            getString(R.string.cn_authok_client_id),
            getString(R.string.cn_authok_domain)
        )
        // Only enable network traffic logging on production environments!
        account.networkingClient = DefaultClient(enableLogging = true)
        account
    }

    private val apiClient: AuthenticationAPIClient by lazy {
        AuthenticationAPIClient(account)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDatabaseLoginBinding.inflate(inflater, container, false)
        binding.buttonLogin.setOnClickListener {
            val username = binding.textUsername.text.toString()
            val password = binding.textPassword.text.toString()
            dbLogin(username, password)
        }
        binding.buttonWebAuth.setOnClickListener {
            webAuth()
        }
        binding.buttonWebLogout.setOnClickListener {
            webLogout()
        }
        return binding.root
    }

    private fun dbLogin(username: String, password: String) {
        apiClient.login(username, password, "database")
            //Additional customization to the request goes here
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    Snackbar.make(requireView(), error.getDescription(), Snackbar.LENGTH_LONG)
                        .show()
                }

                override fun onSuccess(result: Credentials) {
                    Snackbar.make(
                        requireView(),
                        "成功: ${result.accessToken}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun webAuth() {
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.cn_authok_scheme))
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    Snackbar.make(
                        requireView(),
                        "Success: ${result.accessToken}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onFailure(error: AuthenticationException) {
                    val message =
                        if (error.isCanceled) "Browser was closed" else error.getDescription()
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                }
            })
    }

    private fun webLogout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.cn_authok_scheme))
            .start(requireContext(), object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    Snackbar.make(
                        requireView(),
                        "Logged out",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onFailure(error: AuthenticationException) {
                    val message =
                        if (error.isCanceled) "Browser was closed" else error.getDescription()
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                }

            })
    }
}