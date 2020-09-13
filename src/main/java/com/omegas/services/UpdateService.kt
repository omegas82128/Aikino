package com.omegas.services
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.g00fy2.versioncompare.Version
import com.omegas.graphql.GetVersionInfoQuery
import com.omegas.util.AlertType
import com.omegas.util.Constants.APP_NAME
import com.omegas.util.Constants.VERSION
import com.omegas.util.functions.showMessage
import okhttp3.OkHttpClient
import java.awt.Desktop
import java.net.URI
import java.util.*
import kotlin.concurrent.thread


class UpdateService {
    companion object{
        fun automaticStart(){
            start(wasRequested = false)
        }
        fun start(wasRequested:Boolean = true){
            thread(isDaemon = true){
                val client = ApolloClient.builder()
                    .serverUrl("https://feasible-manatee-80.hasura.app/v1/graphql")
                    .okHttpClient(OkHttpClient.Builder().build()).build()

                client.query(GetVersionInfoQuery(APP_NAME))
                .enqueue(object : ApolloCall.Callback<Optional<GetVersionInfoQuery.Data>>() {
                    override fun onResponse(response: Response<Optional<GetVersionInfoQuery.Data>>) {
                        val versionInDB = response.data!!.get().applications_by_pk.get().app_version
                        println(versionInDB)
                        if(Version(versionInDB).isHigherThan(VERSION)){
                            showMessage(title = "Aikino v$versionInDB Available",text = "Click to Download",type = AlertType.INFO){
                                if (Desktop.isDesktopSupported() ) {
                                    Desktop.getDesktop().browse(URI(response.data!!.get().applications_by_pk.get().download_link.get()))
                                }
                            }
                        }else{
                            if(wasRequested){
                                showMessage("You already have the latest Version",AlertType.INFO, "No New Updates Available")
                            }
                        }
                    }

                    override fun onFailure(e: ApolloException) {
                        println(e)
                    }
                })
            }
        }
    }
}