package com.rotarola.portafolio_kotlin.presentation.view.organisms

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotarola.portafolio_kotlin.R

@Composable
fun selectorMapNavigation(
    context: Context,
    latitude:String,
    longitude:String,
    latitudeClient:String,
    longitudeClient:String,
)
{
    Log.e("REOS", "ContentDialog-contentMapNavigation-latitude: "+latitude)
    Log.e("REOS", "ContentDialog-contentMapNavigation-longitude: "+longitude)
    Log.e("REOS", "ContentDialog-contentMapNavigation-latitudeClient: "+latitudeClient)
    Log.e("REOS", "ContentDialog-contentMapNavigation-longitudeClient: "+longitudeClient)
    Row(
        modifier = Modifier
            //.fillMaxHeight(1f)
            .fillMaxWidth(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.mipmap.waze_logo),
                contentDescription = "Waze", // decorative
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    //Set Image size to 40 dp
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        RoundedCornerShape(
                            topEndPercent = 50,
                            bottomStartPercent = 50,
                            topStartPercent = 50,
                            bottomEndPercent = 50
                        )
                    )
                    .clickable(
                        enabled = true,
                        onClick = {
                            val openURL =
                                Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(
                                //"https://waze.com/ul?q=-11.863631,-77.054802&ll=-11.857904,-77.028424&navigate=yes"
                                "https://waze.com/ul?q=" + latitude + "," + longitude + "&ll=" + latitudeClient + "," + longitudeClient + "&navigate=yes"
                            )

                            Log.e(
                                "REOS",
                                "SomeScreen-DialogScreenOne()-url.waze:" + "https://waze.com/ul?q=" + latitude + "," + longitude + "&ll=" + latitudeClient + "," + longitudeClient + "&navigate=yes"
                            )
                            openURL.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(openURL)
                            Toast
                                .makeText(context, "Waze", Toast.LENGTH_SHORT)
                                .show()
                        })


            )
            Text(
                text = "Waze",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,

                    )
            )
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Image(
                painter = painterResource(id = R.mipmap.google_map_icon_412x412),
                contentDescription = "Google Maps", // decorative
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    //Set Image size to 40 dp
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        RoundedCornerShape(
                            topEndPercent = 50,
                            bottomStartPercent = 50,
                            topStartPercent = 50,
                            bottomEndPercent = 50
                        )
                    )
                    .clickable(
                        enabled = true,
                        onClick = {
                            val gmmIntentUri =
                                //Uri.parse("google.navigation:q=-11.857904,-77.028424&")
                                Uri.parse("google.navigation:q=" + latitudeClient + "," + longitudeClient)
                            Log.e(
                                "REOS",
                                "SomeScreen-DialogScreenOne()-url.googlemaps:" + "google.navigation:q=" + latitudeClient + "," + longitudeClient
                            )
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(mapIntent)
                        })
            )
            Text(
                text = "Google Maps",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                )
            )
        }
    }
}
