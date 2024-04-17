package com.xempre.pressurelesshealth.views.reports.MeasurementList;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ActivityListMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Measurement;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeasurementList extends Fragment {
    private ActivityListMeasurementBinding binding;
    private LineChart lineChart;

    private List<String> xValues;

    private TextView promDiastolic;
    private TextView promSystolic;

    List<Entry> entries1;
    List<Entry> entries2;

    private RecyclerView recyclerView;
    private MeasurementAdapter measurementAdapter;
    private List<Measurement> measurementList = new ArrayList<Measurement>();
    private List<Measurement> measurementListFilter = new ArrayList<Measurement>();
    MaterialDatePicker picker;

    Dialog dialog;
    ApiClient apiClient;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


//        View view = inflater.inflate(R.layout.measurement_list, container, false);

        binding = ActivityListMeasurementBinding.inflate(inflater, container, false);
//        lineChart = binding.chart;
//        apiClient = new ApiClient();

        recyclerView = binding.recyclerView;
        measurementAdapter = new MeasurementAdapter(getContext(), measurementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(measurementAdapter);
        promDiastolic = binding.tvDiasProm;
        promSystolic = binding.tvSysProm;

        binding.cbOnlyAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterList(isChecked);
                calcAverage();
            }
        });

        binding.btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTheme(R.style.ThemeMaterialCalendar)
                        .setTitleText("Seleccionar rango de fechas.")
                        .setSelection(Pair.create(null, null))
                        .build();
                picker.show(getActivity().getSupportFragmentManager(), "TAG");

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        if(Objects.equals(selection.first, selection.second)) binding.tvDateRange.setText("Fecha: "+convertDateToString(selection.first));
                        else binding.tvDateRange.setText("Entre: "+convertDateToString(selection.first)+" - "+convertDateToString(selection.second));
                        callAPI(convertDateToString(selection.first), convertDateToString(selection.second));
                    }
                });
            }
        });

        binding.fabInfoHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.ok_dialog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView title;
                title = dialog.findViewById(R.id.tvTitleOkDialog);
                title.setText("Recomendaciones");

                TextView content;
                content = dialog.findViewById(R.id.tvContentOkDialog);
                content.setText(Html.fromHtml(
                                "Para realizar un seguimiento correcto de sus niveles de presión arterial se recomienda:<br>\n" +
                                "<li>Tómese la presión arterial a la misma hora todos los días, preferiblemente en la mañana antes de tomar sus medicamentos y comer.</li><br>\n" +
                                "<li>Siéntese cómodamente con la espalda apoyada y el brazo a la altura del corazón.</li><br>\n" +
                                "<li>Registre estos valores diarios para mostrarlos a su médico.</li><br>\n" +
                                "<li>Realizar las medidas utilizando el modo <b>Avanzado</b>.</li><br>\n" +
                                "Realizar el seguimiento siguiendo estas recomendaciones puede ayudar a su médico a identificar y prevenir complicaciones."
                ));


                dialog.findViewById(R.id.btnOkDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        binding.tvExportReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = new HashMap<>();
                data.put("nombre", "Juan");
                data.put("edad", "30");

                generateReport(getContext(), data);
            }
        });

        long millis = Instant.now().toEpochMilli();
        callAPI(convertDateToString(millis), convertDateToString(millis));
        // Agrega algunos nombres a la lista
//        listaNombres.add("Juan");
//        listaNombres.add("María");
//        listaNombres.add("Luis");

//        Toast.makeText(getContext(), "PERRITO", Toast.LENGTH_SHORT).show();

        //callAPI();

        return binding.getRoot();

    }

    public void calcAverage(){
        int i = 0;
        float prom1 = 0;
        float prom2 = 0;
        for (Measurement element : measurementList) {
            if(binding.cbOnlyAdvanced.isChecked()){
                if (element.getIsAdvanced()){
                    i +=1;
                    prom1 += element.getDiastolicRecord();
                    prom2 += element.getSystolicRecord();
                    DecimalFormat df = new DecimalFormat("0.00");
                    promDiastolic.setText(df.format(prom2/i)+"");
                    promSystolic.setText(df.format(prom1/i)+"");
                }
            } else {
                i +=1;
                prom1 += element.getDiastolicRecord();
                prom2 += element.getSystolicRecord();
                DecimalFormat df = new DecimalFormat("0.00");
                promDiastolic.setText(df.format(prom2/i)+"");
                promSystolic.setText(df.format(prom1/i)+"");
            }

        }

    }

    private void filterList(boolean isAdvanced) {
        List<Measurement> measurementListFilter = new ArrayList<>();
        for (Measurement measurement : measurementList) {
            if (measurement.getIsAdvanced()) {
                measurementListFilter.add(measurement);
            }
        }

        if (isAdvanced){ measurementAdapter.updateList(measurementListFilter);
            this.measurementListFilter = measurementListFilter;}
        else {measurementAdapter.updateList(measurementList);
        this.measurementListFilter = measurementList;
        }
    }

    public String convertDateToString(Long time){
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(utc.getTime());
    }

    public void callAPI(String startDate, String endDate){
        Log.d("PERRUNO", startDate+" "+endDate);

            MeasurementService measurementService = ApiClient.createService(getContext(), MeasurementService.class,1);

            // calling a method to create a post and passing our modal class.
            Call<List<Measurement>> call = measurementService.getAllByDateRange(startDate+"T00:00:00", endDate+"T23:59:59");
            Log.d("PERRUNO", startDate+" "+endDate);
            // on below line we are executing our method.
            call.enqueue(new Callback<List<Measurement>>() {
                @Override
                public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                    // this method is called when we get response from our api.
                    try {
                        List<Measurement> responseFromAPI = response.body();
                        int i = 0;
                        float prom1 = 0;
                        float prom2 = 0;
//                        assert responseFromAPI != null;
                        clearRecyclerView();
                        if (responseFromAPI.isEmpty()) {
                            if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Measurement element : responseFromAPI) {
                                Log.d("PERRUNO", element.toString());
                                i +=1;
                                Measurement temp = new Measurement(element);
                                measurementList.add(element);
                                prom1 += element.getDiastolicRecord();
                                prom2 += element.getSystolicRecord();
                            }
                            DecimalFormat df = new DecimalFormat("0.00");
                            promDiastolic.setText(df.format(prom2/i)+"");
                            promSystolic.setText(df.format(prom1/i)+"");
                            measurementAdapter.notifyDataSetChanged();
                            filterList(false);
                        }

                    } catch (Exception ignored){
                        if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                        onDestroyView();
                    }
                }

                @Override
                public void onFailure(Call<List<Measurement>> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                    onDestroyView();
                    // setting text to our text view when
                    // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
                }
            });
    }

    public void clearRecyclerView(){
        promDiastolic.setText("0.00");
        promSystolic.setText("0.00");
        int size = measurementList.size();
        measurementList.clear();
        measurementAdapter.notifyItemRangeRemoved(0,size);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MeasurementsList.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    public void generateReport(Context context, Map<String, String> data) {
        try {

            InputStream inputStream = context.getAssets().open("baseReport.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Obtener la primera hoja

            int i = 1;

            for (Measurement element : measurementListFilter) {
                    CellStyle style = workbook.createCellStyle();
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);


                    Row row = sheet.getRow(i); // Fila 3
                    if (row == null) {
                        row = sheet.createRow(i); // Si la fila no existe, créala
                    }
                    Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Columna B
                    cell.setCellValue(element.getMeasurementDate()); // Modificar con la edad proporcionada
                    cell.setCellStyle(style);

                    row = sheet.getRow(i); // Fila 3
                    if (row == null) {
                        row = sheet.createRow(i); // Si la fila no existe, créala
                    }
                    cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Columna B
                    cell.setCellValue(element.getSystolicRecord()); // Modificar con la edad proporcionada
                    cell.setCellStyle(style);

                    row = sheet.getRow(i); // Fila 3
                    if (row == null) {
                        row = sheet.createRow(i); // Si la fila no existe, créala
                    }
                    cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Columna B
                    cell.setCellValue(element.getDiastolicRecord()); // Modificar con la edad proporcionada
                    cell.setCellStyle(style);

                    row = sheet.getRow(i); // Fila 3
                    if (row == null) {
                        row = sheet.createRow(i); // Si la fila no existe, créala
                    }
                    cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Columna B
                    cell.setCellValue(element.getIsAdvanced()?"AVANZADA":"BÁSICA"); // Modificar con la edad proporcionada
                    cell.setCellStyle(style);

                    row = sheet.getRow(i); // Fila 3
                    if (row == null) {
                        row = sheet.createRow(i); // Si la fila no existe, créala
                    }
                    cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Columna B
                    cell.setCellValue(element.getComments()); // Modificar con la edad proporcionada
                    cell.setCellStyle(style);

                    i +=1;

            }


            Log.d("PERRUNO", "PARTE4");
            // Obtener la ruta de la carpeta de descargas
            String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            Log.d("PERRUNO", "PARTE5");
            // Guardar el archivo modificado en la carpeta de descargas
            File outputFile = new File(downloadsPath, "Reporte_PressurelessHealth.xlsx");



            FileOutputStream fileOut = new FileOutputStream(outputFile);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            Toast.makeText(context, "Archivo guardado en Descargas (Reporte_PressurelessHealth.xlsx)", Toast.LENGTH_LONG).show();
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroyView() {
        if (binding!=null) binding.btnChangeDate.setClickable(false);
        super.onDestroyView();
        binding = null;
    }
}