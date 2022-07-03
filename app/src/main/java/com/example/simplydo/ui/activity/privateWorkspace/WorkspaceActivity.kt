package com.example.simplydo.ui.activity.privateWorkspace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplydo.R
import com.example.simplydo.databinding.ActivityWorkspaceBinding
import com.example.simplydo.utils.AppConstant
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException


private val TAG = "WorkspaceActivity"

class WorkspaceActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityWorkspaceBinding
    private lateinit var mSocket: Socket

    init {

        try {
            mSocket = IO.socket(AppConstant.Network.SOCKET_BASE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@WorkspaceActivity, R.layout.activity_workspace)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
    }

    private val onConnect = Emitter.Listener {
        Log.i(TAG, "onConnect: ${mSocket.isActive}")
    }
    private val onDisconnect = Emitter.Listener {
        Log.i(TAG, "onDisconnect: ${mSocket.isActive}")
    }

    override fun onResume() {
        super.onResume()
        mSocket.connect();
    }

    override fun onPause() {
        super.onPause()
        mSocket.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }


}