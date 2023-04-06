package com.maestronic.gtfs.bean;

import com.maestronic.gtfs.util.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Object> getErrorPath(HttpServletResponse response) {
        return new ResponseEntity<>(
                ResponseMessage.exceptionErrorJson(
                        response.getStatus(),
                        "Something went wrong!"
                ),
                HttpStatus.valueOf(response.getStatus())
        );
    }
}
