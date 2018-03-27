package com.zaylabs.truckitzaylabsv1driver.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zaylabs.truckitzaylabsv1driver.R;

/**
 * Created by Adil Raza on 3/15/2018.
 */



public class RequestHolder extends RecyclerView.ViewHolder{

    private final TextView customerID;
    /*private final TextView customerName;
    private final TextView customerPhone;
    private final TextView pickupLocation;
    private final TextView dropLocation;
*/
     public RequestHolder(View itemView) {
        super(itemView);

         customerID=(TextView)itemView.findViewById(R.id.customerid);
  /*       customerPhone=(TextView)itemView.findViewById(R.id.customerphone);
         pickupLocation=(TextView)itemView.findViewById(R.id.pickuplocation);
         customerName=(TextView)itemView.findViewById(R.id.customername);
         dropLocation=(TextView)itemView.findViewById(R.id.droplocation);
*/
     }

     public void setCustomerID(String g){
         customerID.setText(g);
     }
  /*  public void setCustomerName(String g){
        customerName.setText(g);
    }
    public void setCustomerPhone(String g){
        customerID.setText(g);
    }
    public void setPickupLocation(String g){
        customerID.setText(g);
    }
    public void setDropLocation(String g){
        customerID.setText(g);
    }
*/}
