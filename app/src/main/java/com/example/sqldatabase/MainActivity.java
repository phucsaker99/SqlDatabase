package com.example.sqldatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.sqldatabase.adapter.PetAdapter;
import com.example.sqldatabase.data.SQLite;
import com.example.sqldatabase.databinding.ActivityMainBinding;
import com.example.sqldatabase.databinding.UpdatePetBinding;
import com.example.sqldatabase.model.Pet;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PetAdapter.PetItemClickListener {

    private ActivityMainBinding binding;
    private SQLite sqLite;
    private PetAdapter adapter;
    private UpdatePetBinding bindingUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initEstablish(); //thiết lập đối tượng
        initEvent(); //thiết lập sự kiện
    }

    private void initEstablish() {
        sqLite = new SQLite(this);
        adapter = new PetAdapter(getLayoutInflater());
        adapter.setPets(sqLite.getAllItems());

        binding.lv.setAdapter(adapter);
    }

    private void initEvent() {
        binding.btnTong.setOnClickListener(this);
        binding.btnThem.setOnClickListener(this);
        binding.btnDong.setOnClickListener(this);
        adapter.setListener(this); //xử lý sự kiện của item trong adapter trực tiếp tại lớp
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tong:
                timTong();
                break;
            case R.id.btn_them:
                themPet();
                break;
            case R.id.btn_dong:
                System.exit(0);
                break;
        }
    }

    // TODO: 11/21/2020 Tìm tổng thú cưng có trong csdl
    private void timTong() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Shop hiện có: "+sqLite.getItemsCount()+" thú cưng")
                .setTitle("Thông báo")
                .setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // TODO: 11/21/2020 Thêm pet vào csdl
    private void themPet() {
        if (isValidator())
            return;
        String name = binding.edtName.getText().toString();
        float weight = Float.parseFloat(binding.edtWeight.getText().toString());
        double price = Double.parseDouble(binding.edtPrice.getText().toString());
        String description = binding.edtDescription.getText().toString().isEmpty()==true?"Không có":binding.edtDescription.getText().toString();
        Pet pet = new Pet(name, weight, price, description);

        sqLite.addItem(pet);
        formatInformation();
        adapter.setPets(sqLite.getAllItems());
    }

    // TODO: 11/21/2020 Làm mới dữ liệu nhập
    private void formatInformation() {
        binding.edtName.setText("");
        binding.edtWeight.setText("");
        binding.edtPrice.setText("");
        binding.edtDescription.setText("");
    }

    // TODO: 11/21/2020 Xử lý ngoại lệ khi thêm pet
    private boolean isValidator() {
        if (binding.edtName.getText().toString().isEmpty()||
        binding.edtWeight.getText().toString().isEmpty()||
        binding.edtPrice.getText().toString().isEmpty()){
            Toast.makeText(this, "Bạn chưa nhập đủ thông tin ", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    // TODO: 11/21/2020 Xử lý ngoại lệ khi cập nhật pet
    private boolean isValidatorUpdate() {
        if (bindingUpdate.edtName.getText().toString().isEmpty()||
                bindingUpdate.edtWeight.getText().toString().isEmpty()||
                bindingUpdate.edtPrice.getText().toString().isEmpty()){
            Toast.makeText(this, "Bạn chưa nhập đủ thông tin ", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }


    // TODO: 11/21/2020 2 phương thực kế thừa để xử lý update và xóa đối tượng khỏi csdl
    @Override
    public void onPetUpdateClicked(final Pet item) {
        bindingUpdate = UpdatePetBinding.inflate(LayoutInflater.from(this)); //Thiết lập data binding cho dialog

        bindingUpdate.edtName.setText(item.getName());
        bindingUpdate.edtWeight.setText(String.valueOf(item.getWeight()));
        bindingUpdate.edtPrice.setText(String.valueOf(item.getPrice()));
        bindingUpdate.edtDescription.setText(item.getDescription().isEmpty()?"":item.getDescription());
                final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(bindingUpdate.getRoot()) //truyền vào view cho phép dùng dataBinding
                .show();
        bindingUpdate.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidatorUpdate()){
                    return;
                }
                item.setName(bindingUpdate.edtName.getText().toString());
                item.setWeight(Float.parseFloat(bindingUpdate.edtWeight.getText().toString()));
                item.setPrice(Double.parseDouble(bindingUpdate.edtPrice.getText().toString()));
                item.setDescription(bindingUpdate.edtDescription.getText().toString().isEmpty()
                        ? "Không có":bindingUpdate.edtDescription.getText().toString());
                sqLite.updateItem(item);
                dialog.dismiss();
                adapter.setPets(sqLite.getAllItems());
            }
        });
    }

    @Override
    public void onPetDeleteClicked(int position) {
        sqLite.deleteItem(position);
        adapter.setPets(sqLite.getAllItems()); //để cập nhật lại dữ liệu trong adapter
    }
}