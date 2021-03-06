package com.example.hp.markmep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Product> productList;


    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cardview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Product product = productList.get(position);

        //binding the data with the viewholder views
        holder.subject.setText(product.getSubject());
        holder.description.setText(product.getDetails());

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView subject, description;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            subject = itemView.findViewById(R.id.subtv);
            description = itemView.findViewById(R.id.detailtv);
        }
    }
}
