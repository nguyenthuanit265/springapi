package com.learn.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBillsByProviderRequest {
    private String provider;
}
