package fraud.detection.app.controllers;

import fraud.detection.app.dto.SendMoneyDTO;
import org.springframework.web.client.RestTemplate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

public class MyApiListener {

    @Scheduled(fixedRate = 5000) // poll every 5 seconds
    public void listenToApi() {
    RestTemplate restTemplate = new RestTemplate();
    String apiUrl = "http://192.168.137.1:8000/classify";
    SendMoneyDTO response = restTemplate.getForObject(apiUrl, SendMoneyDTO.class);
    System.out.println(response);

//    SendMoneyDTO sendMoneyDTO = new SendMoneyDTO();
//    sendMoneyDTO.getTransactionAmount(response.getData().getToken());
//    myData.setUserPhoneNumber(response.getData().getUserPhoneNumber());
//    myData.setUserName(response.getData().getUserName());
//    myData.setBalance(response.getData().getBalance());
//    myDataRepository.save(myData);
    }
}