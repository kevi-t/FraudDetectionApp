package fraud.detection.app.controllers;

import fraud.detection.app.dto.LipaTillDto;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.LipaTillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("fraud/app")
public class LipaTill {

    private final LipaTillService lipaTillService;

    public LipaTill(LipaTillService lipaTillService) {
        this.lipaTillService = lipaTillService;
    }

    @PostMapping(path = "/lipatill", produces = "application/json")
    public ResponseEntity<UniversalResponse> lipaNaTill(@RequestBody LipaTillDto request){
        return ResponseEntity.ok(lipaTillService.lipaTill(request));
    }
}
