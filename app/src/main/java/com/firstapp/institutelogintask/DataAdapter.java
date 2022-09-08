package com.firstapp.institutelogintask;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    List<DataModel>dataModelList;
    Context context;
    AlertDialog alertDialog;

    public DataAdapter(List<DataModel> dataModelList, Context context) {
        this.dataModelList = dataModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_listview,parent,false);

        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
        holder.name.setText(dataModelList.get(position).getName());
        holder.content.setText(dataModelList.get(position).getContent());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View root = LayoutInflater.from(context).inflate(R.layout.alertdialog,null);




                Button cancel=root.findViewById(R.id.alertcancel);
                Button delete=root.findViewById(R.id.alretdelete);
                Button update=root.findViewById(R.id.alertupdate);

                builder.setView(root);
                builder.setCancelable(false);





                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();



                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();


                    }
                });
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }
                });

                alertDialog=builder.create();
                alertDialog.show();
            }
        });


    }




    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,content;
//

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.recyclername);
            content=itemView.findViewById(R.id.recycelrcontent);
        }
    }
}
