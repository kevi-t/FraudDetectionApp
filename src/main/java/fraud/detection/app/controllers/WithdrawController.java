////package fraud.detection.app.controllers;
////
////import fraud.detection.app.dto.WithdrawDTO;
////import fraud.detection.app.services.WithdrawService;
////import jakarta.validation.Valid;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.validation.annotation.Validated;
////import org.springframework.web.bind.annotation.PostMapping;
////import org.springframework.web.bind.annotation.RequestBody;
////import org.springframework.web.bind.annotation.RequestMapping;
////import org.springframework.web.bind.annotation.RestController;
////
////@RestController
////@RequestMapping("/fraud/app")
////@Validated
////@Slf4j
////public class WithdrawController {
////
////    private final WithdrawService withdrawService;
////    @Autowired
////    public WithdrawController( WithdrawService withdrawService) {
////        this.withdrawService = withdrawService;
////    }
////
////    @PostMapping("/withdraw")
////    public ResponseEntity<?> withdrawMoney(@Valid @RequestBody WithdrawDTO request ) {
////        try{
////            return ResponseEntity.ok(withdrawService.withdrawMoney(request));
////        }
////        catch (Exception ex){
////            return new ResponseEntity(HttpStatus.BAD_REQUEST);
////        }
////    }
//}