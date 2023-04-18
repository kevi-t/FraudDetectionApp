package fraud.detection.app.controllers;

import fraud.detection.app.dto.LipaBillDto;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.LipaBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("fraud/app")
public class LipaBill {
    private final LipaBillService lipaBillService;
    public LipaBill(LipaBillService lipaBillService) {
        this.lipaBillService = lipaBillService;
    }

    @PostMapping(path = "/lipabill", produces = "application/json")
    public ResponseEntity<UniversalResponse> lipaBill(@RequestBody LipaBillDto request){
        return ResponseEntity.ok(lipaBillService.lipaBill(request));
    }
}
