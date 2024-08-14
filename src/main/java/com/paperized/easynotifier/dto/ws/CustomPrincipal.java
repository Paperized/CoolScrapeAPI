package com.paperized.easynotifier.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomPrincipal implements Principal {
    private String name;
    private boolean anonymous;
}
