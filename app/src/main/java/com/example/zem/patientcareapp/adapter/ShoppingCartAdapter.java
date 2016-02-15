package com.example.zem.patientcareapp.adapter;

import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.ShoppingCartActivity;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Customizations.RoundedAvatarDrawable;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingCartAdapter extends ArrayAdapter implements View.OnClickListener {
    LayoutInflater inflater;
    Context context;

    ImageView prod_image;
    ImageButton delete, up_btn, down_btn;
    LinearLayout root;
    TextView productName, product_quantity, product_price, total, is_promo, promo_total, free_item;

    Helpers helpers;
    DbHelper dbHelper;

    ArrayList<HashMap<String, String>> objects;

    public static double cart_total_amount;
    public static double total_savings_value;
    DecimalFormat df;

    public ShoppingCartAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.objects = objects;

        helpers = new Helpers();
        dbHelper = new DbHelper(context);
        cart_total_amount = 0.0;
        total_savings_value = 0.0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_shopping_cart, parent, false);

        prod_image = (ImageView) convertView.findViewById(R.id.prod_image);
        productName = (TextView) convertView.findViewById(R.id.productName);
        product_price = (TextView) convertView.findViewById(R.id.product_price);
        product_quantity = (TextView) convertView.findViewById(R.id.product_quantity);
        is_promo = (TextView) convertView.findViewById(R.id.is_promo);
        total = (TextView) convertView.findViewById(R.id.total);
        delete = (ImageButton) convertView.findViewById(R.id.delete);
        up_btn = (ImageButton) convertView.findViewById(R.id.up_btn);
        down_btn = (ImageButton) convertView.findViewById(R.id.down_btn);
        root = (LinearLayout) convertView.findViewById(R.id.root);
        promo_total = (TextView) convertView.findViewById(R.id.promo_total);
        free_item = (TextView) convertView.findViewById(R.id.free_item);

        delete.setTag(position);
        up_btn.setTag(product_quantity);
        down_btn.setTag(product_quantity);

        final double price = Double.parseDouble(objects.get(position).get("price"));
        int qty_per_packing = Integer.parseInt(objects.get(position).get("qty_per_packing"));
        final int available_qty = Integer.parseInt(objects.get(position).get("available_quantity"));
        final int cart_quantity = Integer.parseInt(objects.get(position).get("quantity"));

        final double total_per_item = price * cart_quantity;
        df = new DecimalFormat("#.##");

        int final_qty_required = 0, final_percentage = 0;
        double final_peso = 0, final_min_purchase = 0;
        String final_is_every = "0", final_free_gift = "0", final_free_packing = "", final_free_item_name = "", final_free_qty = "", final_free_prod_price = "";

        cart_total_amount = cart_total_amount + total_per_item;

        if (ShoppingCartActivity.no_code_promos.size() > 0) {
            for (int x = 0; x < ShoppingCartActivity.no_code_promos.size(); x++) {
                if (ShoppingCartActivity.no_code_promos.get(x).get("product_id").equals(objects.get(position).get("product_id"))) {
                    final_min_purchase = Double.parseDouble(ShoppingCartActivity.no_code_promos.get(x).get("minimum_purchase"));
                    final_qty_required = Integer.parseInt(ShoppingCartActivity.no_code_promos.get(x).get("quantity_required"));
                    final_percentage = Integer.parseInt(ShoppingCartActivity.no_code_promos.get(x).get("percentage_discount"));
                    final_peso = Double.parseDouble(ShoppingCartActivity.no_code_promos.get(x).get("peso_discount"));
                    final_free_gift = ShoppingCartActivity.no_code_promos.get(x).get("has_free_gifts");
                    final_is_every = ShoppingCartActivity.no_code_promos.get(x).get("is_every");
                    final_free_qty = ShoppingCartActivity.no_code_promos.get(x).get("quantity_free");
                    final_free_packing = ShoppingCartActivity.no_code_promos.get(x).get("free_product_packing");
                    final_free_item_name = ShoppingCartActivity.no_code_promos.get(x).get("name");
                    final_free_prod_price = ShoppingCartActivity.no_code_promos.get(x).get("free_prod_price");

                    String type_of_promo = "", type_of_minimum = "";
                    String purchases = helpers.getPluralForm(objects.get(position).get("packing"), final_qty_required);

                    if (final_qty_required > 0) {
                        double saved_amount = 0;

                        if (final_is_every.equals("1"))
                            type_of_minimum = " for every " + final_qty_required + " " + purchases;
                        else
                            type_of_minimum = " for " + final_qty_required + " " + purchases + " or more";

                        if (!final_free_gift.equals("0")) {
                            type_of_promo = "*A free item";

                            if (cart_quantity >= final_qty_required) {
                                free_item.setVisibility(View.VISIBLE);
                                String free_item_purchase;
                                int qty;

                                if (final_is_every.equals("1")) {
                                    int discount_times = cart_quantity / final_qty_required;

                                    qty = discount_times;
                                    free_item_purchase = helpers.getPluralForm(final_free_packing, discount_times);
                                    saved_amount = (discount_times * Double.parseDouble(final_free_prod_price));
                                } else {
                                    qty = Integer.parseInt(final_free_qty);
                                    free_item_purchase = helpers.getPluralForm(final_free_packing, Integer.parseInt(final_free_qty));
                                    saved_amount = Double.parseDouble(final_free_prod_price);
                                }

                                String set_free_item = "*Free " + qty + " " + free_item_purchase + " of " + final_free_item_name;
                                free_item.setText(set_free_item);
                                total_savings_value += saved_amount;
                            }
                        }
                    } else if (final_min_purchase > 0) {
                        int discount_times = (int) (total_per_item / final_min_purchase);
                        double discounted_amount = 0, discounted_total = 0;

                        if (final_is_every.equals("1"))
                            type_of_minimum = " for every Php " + final_min_purchase + " worth of purchase";
                        else
                            type_of_minimum = " for a minimum purchase of Php " + final_min_purchase;

                        if (final_peso > 0) {
                            type_of_promo = "*Php " + final_peso + " off";

                            if (total_per_item >= final_min_purchase) {
                                promo_total.setVisibility(View.VISIBLE);
                                total.setPaintFlags(total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                if (final_is_every.equals("1")) {
                                    discounted_total = total_per_item - (discount_times * final_peso);
                                    discounted_amount = total_per_item - discounted_total;
                                } else {
                                    discounted_total = total_per_item - final_peso;
                                    discounted_amount = final_peso;
                                }
                            }
                        } else if (final_percentage > 0 && final_is_every.equals("0")) {
                            type_of_promo = "*" + final_percentage + "% off";

                            if (total_per_item >= final_min_purchase) {
                                promo_total.setVisibility(View.VISIBLE);
                                total.setPaintFlags(total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                double percent_off = Double.parseDouble(String.valueOf(final_percentage / 100.0f));

                                discounted_amount = total_per_item * percent_off;
                                discounted_total = total_per_item - discounted_amount;
                            }
                        }

                        promo_total.setText("Php " + df.format(discounted_total));
                        cart_total_amount = cart_total_amount - discounted_amount;
                        total_savings_value += discounted_amount;
                    }

                    if (!type_of_promo.equals("")) {
                        is_promo.setVisibility(View.VISIBLE);
                        is_promo.setText(type_of_promo + type_of_minimum);
                    }
                }
            }
        }

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ambrolex_family);
        final int shadowSize = context.getResources().getDimensionPixelSize(R.dimen.shadow_size);
        final int shadowColor = context.getResources().getColor(R.color.shadow_color);
        prod_image.setImageDrawable(new RoundedAvatarDrawable(icon, shadowSize, shadowColor));
        prod_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String plural = helpers.getPluralForm(objects.get(position).get("unit"), qty_per_packing);
        product_price.setText("Php " + price + "/" + objects.get(position).get("packing") + " (" + qty_per_packing + " " + plural + ")");
        productName.setText(objects.get(position).get("name"));
        total.setText("Php " + df.format(total_per_item));
        product_quantity.setText(String.valueOf(cart_quantity));

        delete.setOnClickListener(this);

        final View finalConvertView = convertView;
        final int final_qty_required1 = final_qty_required;
        final int final_percentage1 = final_percentage;
        final double final_peso1 = final_peso;
        final String final_is_every1 = final_is_every;
        final double final_min_purchase1 = final_min_purchase;
        final String final_free_gift1 = final_free_gift;
        final String final_free_qty1 = final_free_qty;
        final String final_free_packing1 = final_free_packing;
        final String final_free_item_name1 = final_free_item_name;

        final double decimal = (100 - final_percentage1) / 100.0f;
        final double percent_off = Double.parseDouble(String.valueOf(final_percentage1 / 100.0f));
        final Object txt_promo_total = promo_total;
        final Object txt_free_item = free_item;

        final String final_free_prod_price1 = final_free_prod_price;
        up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = (TextView) v.getTag();
                TextView txt_promo = (TextView) txt_promo_total;
                TextView txt_obj_free_item = (TextView) txt_free_item;
                int lastQty = Integer.parseInt(txt.getText().toString());
                TextView p_total = (TextView) finalConvertView.findViewById(R.id.total);

                lastQty = lastQty + 1;

                if (lastQty <= available_qty) {
                    double total_per_item = price * lastQty;
                    txt.setText(String.valueOf(lastQty));

                    if (final_qty_required1 > 0 && final_free_gift1.equals("1")) {
                        cart_total_amount = cart_total_amount + price;

                        if (lastQty >= final_qty_required1) {
                            txt_obj_free_item.setVisibility(View.VISIBLE);
                            int discount_times = lastQty / final_qty_required1;
                            String set_free_item;
                            double saved = 0;

                            if (final_is_every1.equals("1")) {
                                String purchases = helpers.getPluralForm(final_free_packing1, discount_times);
                                set_free_item = "*Free " + discount_times + " " + purchases + " of " + final_free_item_name1;

                                if (lastQty % final_qty_required1 == 0)
                                    saved = Double.parseDouble(final_free_prod_price1);
                            } else {
                                set_free_item = "*Free " + final_free_qty1 + " " + final_free_packing1 + " of " + final_free_item_name1;

                                if (lastQty == final_qty_required1)
                                    saved = Integer.parseInt(final_free_qty1) * Double.parseDouble(final_free_prod_price1);
                            }

                            total_savings_value += saved;
                            txt_obj_free_item.setText(set_free_item);
                        }

                    } else if (final_min_purchase1 > 0) {
                        int discount_times = (int) (total_per_item / final_min_purchase1);

                        if (total_per_item >= final_min_purchase1) {
                            txt_promo.setVisibility(View.VISIBLE);
                            p_total.setPaintFlags(p_total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            double temp_prod_discount = 0;

                            if (final_percentage1 > 0 && final_is_every1.equals("0")) {
                                temp_prod_discount = total_per_item * decimal;

                                if ((total_per_item - price) < final_min_purchase1) {
                                    total_savings_value += total_per_item * percent_off;
                                    cart_total_amount += (temp_prod_discount - (total_per_item - price));
                                } else {
                                    cart_total_amount += temp_prod_discount - ((total_per_item - price) * decimal);
                                    total_savings_value += price * percent_off;
                                }
                            } else if (final_peso1 > 0) {
                                if (final_is_every1.equals("1")) {
                                    temp_prod_discount = total_per_item - (discount_times * final_peso1);

                                    if ((total_per_item - price) < (final_min_purchase1 * discount_times)) {
                                        cart_total_amount += (price - final_peso1);
                                        total_savings_value += final_peso1;
                                    } else
                                        cart_total_amount += price;
                                } else {
                                    temp_prod_discount = total_per_item - final_peso1;
                                    cart_total_amount += price;

                                    if ((total_per_item - price) < final_min_purchase1) {
                                        cart_total_amount -= final_peso1;
                                        total_savings_value += final_peso1;
                                    }
                                }
                            }

                            txt_promo.setText("₱ " + df.format(temp_prod_discount));
                        } else
                            cart_total_amount = cart_total_amount + price;
                    } else if (final_qty_required1 == 0 && final_min_purchase1 == 0)
                        cart_total_amount = cart_total_amount + price;

                    p_total.setText("Php " + df.format(total_per_item));
                    ShoppingCartActivity.total_amount.setText("Total amount is ₱" + df.format(cart_total_amount));
                    ShoppingCartActivity.total_savings.setText("You saved ₱" + df.format(total_savings_value));

                    HashMap<String, String> temp = objects.get(position);
                    temp.put("quantity", String.valueOf(lastQty));
                    objects.set(position, temp);
                } else
                    Snackbar.make(v, "Out of stock", Snackbar.LENGTH_SHORT).show();
            }
        });

        down_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = (TextView) v.getTag();
                TextView p_total = (TextView) finalConvertView.findViewById(R.id.total);
                TextView txt_promo = (TextView) txt_promo_total;
                TextView txt_obj_free_item = (TextView) txt_free_item;

                int lastQty = Integer.parseInt(txt.getText().toString());
                lastQty = lastQty - 1;
                double total_per_item = price * 1;


                if (lastQty < 1) {
                    lastQty = 1;
                    ShoppingCartActivity.total_amount.setText("Total amount is ₱" + df.format(cart_total_amount));
                    ShoppingCartActivity.total_savings.setText("You saved ₱" + df.format(total_savings_value));
                } else {
                    total_per_item = price * lastQty;
                    cart_total_amount = cart_total_amount - price;

                    if (final_qty_required1 > 0 && final_free_gift1.equals("1")) {
                        if (lastQty < final_qty_required1) {
                            txt_obj_free_item.setVisibility(View.GONE);
                            total_savings_value -= (Integer.parseInt(final_free_qty1) * Double.parseDouble(final_free_prod_price1));
                        } else {
                            if (final_is_every1.equals("1")) {
                                int discount_times = lastQty / final_qty_required1;
                                String set_free_item;

                                String purchases = helpers.getPluralForm(final_free_packing1, discount_times);
                                set_free_item = "*Free " + discount_times + " " + purchases + " of " + final_free_item_name1;

                                txt_obj_free_item.setText(set_free_item);

                                if (lastQty % final_qty_required1 == 0)
                                    total_savings_value -= Double.parseDouble(final_free_prod_price1);
                            }
                        }

                    } else if (final_min_purchase1 > 0) {
                        if (total_per_item < final_min_purchase1) {
                            txt_promo.setVisibility(View.GONE);
                            p_total.setPaintFlags(p_total.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                            if ((total_per_item + price) >= final_min_purchase1) {
                                if (final_peso1 > 0) {
                                    cart_total_amount += final_peso1;
                                    total_savings_value -= final_peso1;
                                } else if (final_percentage1 > 0 && final_is_every1.equals("0")) {
                                    double back = (total_per_item + price) * percent_off;
                                    cart_total_amount += back;
                                    total_savings_value -= back;
                                }
                            }
                        } else {
                            double discounted_temp_total = price * lastQty;
                            int discount_times = (int) (total_per_item / final_min_purchase1);

                            if (final_peso1 > 0) {
                                if (!final_is_every1.equals("0")) {
                                    discounted_temp_total -= (final_peso1 * discount_times);

                                    if ((total_per_item - price) > (final_min_purchase1 * discount_times)) {
                                        cart_total_amount += final_peso1;
                                        total_savings_value -= final_peso1;
                                    }
                                } else
                                    discounted_temp_total -= final_peso1;
                            } else if (final_percentage1 > 0 && final_is_every1.equals("0")) {
                                discounted_temp_total = discounted_temp_total - ((price * lastQty) * percent_off);
                                cart_total_amount = cart_total_amount + (price * percent_off);
                                total_savings_value -= price * percent_off;
                            }
                            txt_promo.setText("Php " + df.format(discounted_temp_total));
                        }
                    }
                    ShoppingCartActivity.total_amount.setText("Total amount is ₱" + df.format(cart_total_amount));
                    ShoppingCartActivity.total_savings.setText("You saved ₱" + df.format(total_savings_value));
                }

                txt.setText(String.valueOf(lastQty));
                p_total.setText("Php " + df.format(total_per_item));

                HashMap<String, String> temp = objects.get(position);
                temp.put("quantity", String.valueOf(lastQty));
                objects.set(position, temp);
            }
        });

        ShoppingCartActivity.total_amount.setText("Total amount is ₱" + df.format(cart_total_amount));
        ShoppingCartActivity.total_savings.setText("You saved ₱" + df.format(total_savings_value));

        return convertView;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.delete:
                final int pos = Integer.parseInt(String.valueOf(v.getTag()));
                final int server_id = Integer.parseInt(objects.get(pos).get("basket_id"));

                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(context);
                confirmationDialog.setTitle("Delete item?");
                confirmationDialog.setNegativeButton("No", null);
                confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("table", "baskets");
                        hashMap.put("request", "crud");
                        hashMap.put("action", "delete");
                        hashMap.put("id", String.valueOf(server_id));

                        final ProgressDialog pdialog = new ProgressDialog(context);
                        pdialog.setCancelable(false);
                        pdialog.setMessage("Loading...");
                        pdialog.show();

                        PostRequest.send(context, hashMap, new RespondListener<JSONObject>() {
                            @Override
                            public void getResult(JSONObject response) {
                                try {
                                    int success = response.getInt("success");

                                    if (success == 1) {
                                        cart_total_amount = 0;
                                        objects.remove(pos);
                                        ShoppingCartAdapter.this.notifyDataSetChanged();

                                        if (objects.size() == 0) {
                                            ShoppingCartActivity.total_amount.setText("---");
                                            ShoppingCartActivity.total_savings.setText("---");
                                        }

                                        Snackbar.make(v, "Item has been deleted", Snackbar.LENGTH_SHORT).show();
                                    } else
                                        Snackbar.make(v, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    Snackbar.make(v, e + "", Snackbar.LENGTH_SHORT).show();
                                }
                                pdialog.dismiss();
                            }
                        }, new ErrorListener<VolleyError>() {
                            public void getError(VolleyError error) {
                                pdialog.dismiss();
                                Snackbar.make(v, "Network error", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                confirmationDialog.show();
                break;
        }
    }
}
