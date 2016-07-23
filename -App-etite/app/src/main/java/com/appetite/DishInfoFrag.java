package com.appetite;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pravallika on 3/9/16.
 */
public class DishInfoFrag extends Fragment {
    ImageView chefDishPic;
    TextView chefIngredients, chefQuantity;
    EditText quantityInput;
    String quantityOrdered, chefName, chef, dish, quan, foodImage,quantityAvbl;
    Button orderButton, bt_orderInc, bt_orderDec;
    Firebase mRef, mRef1, mRef2, orderRef;
    int remainingQuantity = 0;
    String imagePath;
    int count = 0;
    final static private String APP_KEY = "ya6xzn1c4rjsjdu";
    final static private String APP_SECRET = "tka1puxynswkeek";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    int price = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session =
                new AndroidAuthSession(keyPair,
                        "bu-o0D7UlrAAAAAAAAAACqbhdtpfpEG8J1_KrFJWnCLfyxlnJ8q_44LSiXkk1yig");
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        View v = inflater.inflate(R.layout.tab_dish_info, container, false);
        chefDishPic = (ImageView) v.findViewById(R.id.chefDishPic);

        imagePath = DishDetailsActivity.chefFoodImage;

        if (imagePath.equals("")) {
            chefDishPic.setImageResource(R.drawable.caesarsalad);
        } else {
            AsyncTask<String, Void, ByteArrayOutputStream> downloadHandler = new AsyncTask<String, Void, ByteArrayOutputStream>() {

                @Override
                protected ByteArrayOutputStream doInBackground(String... params) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile(imagePath, null, byteArrayOutputStream, null);
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return byteArrayOutputStream;
                }

                @Override
                protected void onPostExecute(ByteArrayOutputStream outputStream) {
                    byte[] outputArray = outputStream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(outputArray, 0, outputArray.length);
                    chefDishPic.setImageBitmap(bitmap);
                }
            };

            downloadHandler.execute();
        }

        chefIngredients = (TextView) v.findViewById(R.id.chefIngredients);
        chefIngredients.setText(DishDetailsActivity.chefDishIngredients);
        chefQuantity = (TextView) v.findViewById(R.id.chefQuantity);

        chefQuantity.setText(DishDetailsActivity.chefDishQuantity);

        quantityInput = (EditText) v.findViewById(R.id.etUserQuantity);
        quantityInput.setText(String.valueOf(count));

        bt_orderInc = (Button) v.findViewById(R.id.bt_increment);
        bt_orderInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                quantityInput.setText(String.valueOf(count));
            }
        });

        bt_orderDec = (Button) v.findViewById(R.id.bt_decrement);
        bt_orderDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                quantityInput.setText(String.valueOf(count));
            }
        });

        orderButton = (Button) v.findViewById(R.id.btOrder);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();

            }
        });
        return v;
    }


    public void placeOrder() {
        quantityOrdered = quantityInput.getText().toString();

        remainingQuantity = Integer.parseInt(DishDetailsActivity.chefDishQuantity) - Integer.parseInt(quantityOrdered);
        showAlertDialog();
        chefQuantity.setText(String.valueOf(remainingQuantity));
        Firebase.setAndroidContext(getContext());
        mRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/" + DishDetailsActivity.chefName + "/");

        Map<String, Object> quantityOrderedMap = new HashMap<String, Object>();
        quantityOrderedMap.put(DishDetailsActivity.chefDishName + "/quantityOrdered", quantityOrdered);
        quantityOrderedMap.put(DishDetailsActivity.chefDishName + "/quantity", String.valueOf(remainingQuantity));
        mRef.updateChildren(quantityOrderedMap);

        mRef2 = new Firebase("https://app-etite.firebaseio.com/notifyUsers/");
        mRef2.child("user").setValue(Login.userName);

        MainActivity.cookModule = false;

    }

    public void showAlertDialog() {

        price = Integer.parseInt(quantityOrdered) * 5;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Order Confirmation");
        alertDialog.setMessage("Price: " + "$ " + price + "\n" + "Please confirm the order.");
        //positive response
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRef1 = new Firebase("https://app-etite.firebaseio.com/orders/");
                Map<String, Object> orders = new HashMap<String, Object>();
                orders.put("chef", DishDetailsActivity.chefName);
                orders.put("customer", Login.userName);
                orders.put("dish", DishDetailsActivity.chefDishName);
                orders.put("quantity", quantityOrdered);
                mRef1.updateChildren(orders);
                mRef1 = new Firebase("https://app-etite.firebaseio.com/orders");


                Intent orderConfirm = new Intent(getActivity(), OrderConfirmation.class);
                orderConfirm.putExtra("chefName", DishDetailsActivity.chefName);
                orderConfirm.putExtra("chefDish",DishDetailsActivity.chefDishName);
                orderConfirm.putExtra("quantity",quantityOrdered);
                orderConfirm.putExtra("price",String.valueOf(price));
                startActivity(orderConfirm);

                quantityInput.setText(" ");
            }
        });
        alertDialog.create().show();

    }
}
