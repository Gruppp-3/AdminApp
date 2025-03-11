package com.example.restaurantorderapp.api;

import com.example.restaurantorderapp.model.Employee;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static RetrofitClient instance;
    private Retrofit retrofit;

    // Custom deserializer for Employee to handle API field naming
    private static class EmployeeDeserializer implements JsonDeserializer<Employee> {
        @Override
        public Employee deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Employee employee = new Employee();

            // Handle possible variations in field names from the API
            if (jsonObject.has("employeeId")) {
                employee.setId(jsonObject.get("employeeId").getAsLong());
            } else if (jsonObject.has("id")) {
                employee.setId(jsonObject.get("id").getAsLong());
            } else if (jsonObject.has("employee_id")) {
                employee.setId(jsonObject.get("employee_id").getAsLong());
            }

            if (jsonObject.has("firstName")) {
                employee.setFirstName(jsonObject.get("firstName").getAsString());
            } else if (jsonObject.has("first_name")) {
                employee.setFirstName(jsonObject.get("first_name").getAsString());
            }

            if (jsonObject.has("lastName")) {
                employee.setLastName(jsonObject.get("lastName").getAsString());
            } else if (jsonObject.has("last_name")) {
                employee.setLastName(jsonObject.get("last_name").getAsString());
            }

            if (jsonObject.has("phoneNumber")) {
                employee.setPhoneNumber(jsonObject.get("phoneNumber").getAsString());
            } else if (jsonObject.has("phone_number")) {
                employee.setPhoneNumber(jsonObject.get("phone_number").getAsString());
            }

            if (jsonObject.has("isAdmin")) {
                employee.setIsAdmin(jsonObject.get("isAdmin").getAsBoolean());
            } else if (jsonObject.has("is_admin")) {
                employee.setIsAdmin(jsonObject.get("is_admin").getAsBoolean());
            }

            return employee;
        }
    }

    private RetrofitClient() {
        // Create a custom Gson instance with our Employee deserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Employee.class, new EmployeeDeserializer())
                .create();

        // Create logging interceptor (keeping your existing implementation)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add the interceptor to OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)) // Using our custom gson
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApi() {
        return retrofit.create(ApiService.class);
    }
}