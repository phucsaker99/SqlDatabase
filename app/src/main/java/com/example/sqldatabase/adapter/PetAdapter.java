package com.example.sqldatabase.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqldatabase.R;
import com.example.sqldatabase.databinding.ItemPetBinding;
import com.example.sqldatabase.model.Pet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {
    private ArrayList<Pet> pets;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private PetItemClickListener listener;

    public PetAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
        notifyDataSetChanged();
    }

    public void setListener(PetItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetBinding binding =ItemPetBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Pet pet = pets.get(position);

        //Vẽ từng item vào recyclerView
        holder.binding.itemName.setText(MessageFormat.format("Tên pet: {0}", pet.getName()));
        holder.binding.itemWeight.setText(MessageFormat.format("Cân nặng: {0}", String.valueOf(pet.getWeight())));
        holder.binding.itemPrice.setText(MessageFormat.format("Giá tiền: {0}", String.valueOf(pet.getPrice())));
        holder.binding.itemDescription.setText(MessageFormat.format("Mô tả: {0}", pet.getDescription()));
        holder.binding.itemDate.setText(MessageFormat.format("Cập nhật: {0}", pet.getDate()));

        //Thêm sự kiện khi click vào button tương ứng với mỗi item
        if (listener != null){
            holder.binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPetUpdateClicked(pets.get(position));
                }
            });

            holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPetDeleteClicked(pets.get(position).getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pets == null ?0:pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemPetBinding binding;
        public ViewHolder(@NonNull ItemPetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    //Interface để sự lý khi click
    public interface PetItemClickListener{
        void onPetUpdateClicked(Pet item);
        void onPetDeleteClicked(int position);
    }
}
