package org.soyphea.springbatchkafka.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BatchRetryableException extends RuntimeException{
    String message;
}
