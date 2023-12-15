package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {
	private String code;
	private Date dateStart;
	private Date dateEnd;
	private int amount;
	private Date dateUpdate;
}
