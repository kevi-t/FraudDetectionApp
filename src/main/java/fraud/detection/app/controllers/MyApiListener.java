package fraud.detection.app.controllers;

import org.springframework.web.client.RestTemplate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

    public class MyApiListener {

        @Scheduled(fixedRate = 5000) // poll every 5 seconds
        public void listenToApi() {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://api.example.com/data";
            String response = restTemplate.getForObject(apiUrl, String.class);

//            MyData myData = new MyData();
//            myData.setToken(response.getData().getToken());
//            myData.setUserPhoneNumber(response.getData().getUserPhoneNumber());
//            myData.setUserName(response.getData().getUserName());
//            myData.setBalance(response.getData().getBalance());
//
//            myDataRepository.save(myData);
        }
    }

