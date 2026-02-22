package com.nishakar.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "kafka_events")
public class KafkaEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    @JsonAlias("fname")
    private String fName;
    @Column(name = "last_name")
    @JsonAlias("lname")
    private String lName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String status;
}
