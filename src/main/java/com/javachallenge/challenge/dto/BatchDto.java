package com.javachallenge.challenge.dto;

import lombok.Data;

@Data
public class BatchDto {
	private int total;
	private int imported;
	private int nonImported;

}
