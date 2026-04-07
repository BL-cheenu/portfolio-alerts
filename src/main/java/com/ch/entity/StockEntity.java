package com.ch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "ticker_symbol", nullable = false, unique = true)
    private String tickerSymbol;

    @Column(name = "exchange")
    private String exchange; // NSE / BSE
}
