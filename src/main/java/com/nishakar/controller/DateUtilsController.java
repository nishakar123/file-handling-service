package com.nishakar.controller;

import com.nishakar.commons.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
public class DateUtilsController {

    @GetMapping("/date/example")
    public void show() {
        // ========== Parse dates ==========
        LocalDate date1 = DateUtils.parseToLocalDate("25-12-2024", "dd-MM-yyyy");
        System.out.println("Parsed date: " + date1);

        LocalDate date2 = DateUtils.parseISODate("2024-12-25");
        System.out.println("ISO date: " + date2);

        // ========== Format dates ==========
        String formatted1 = DateUtils.formatToDDMMYYYY(LocalDate.now());
        System.out.println("Formatted (DD-MM-YYYY): " + formatted1);

        String formatted2 = DateUtils.formatToISO(LocalDate.now());
        System.out.println("Formatted (ISO): " + formatted2);

        // ========== Convert between formats ==========
        String converted = DateUtils.convertDateFormat(
                "25/12/2024",           // Input
                "dd/MM/yyyy",           // From format
                "yyyy-MM-dd"            // To format
        );
        System.out.println("Converted: " + converted); // 2024-12-25

        // ========== Parse and format datetime ==========
        LocalDateTime dateTime = DateUtils.parseToLocalDateTime(
                "2024-12-25 14:30:45",
                "yyyy-MM-dd HH:mm:ss"
        );

        String displayDateTime = DateUtils.formatToDisplayDateTime(dateTime);
        System.out.println("Display format: " + displayDateTime); // 25 Dec 2024 14:30:45

        // ========== Convert java.util.Date ==========
        Date utilDate = new Date();
        LocalDate localDate = DateUtils.toLocalDate(utilDate);
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(utilDate);

        Date backToUtilDate = DateUtils.toDate(localDate);

        // ========== Timestamp conversions ==========
        long timestamp = DateUtils.toEpochMilli(LocalDateTime.now());
        System.out.println("Timestamp: " + timestamp);

        LocalDateTime fromTimestamp = DateUtils.fromEpochMilli(timestamp);
        System.out.println("From timestamp: " + fromTimestamp);

        // ========== Current date/time ==========
        String currentDate = DateUtils.getCurrentDateAsString("dd-MM-yyyy");
        System.out.println("Current date: " + currentDate);

        String currentDateTime = DateUtils.getCurrentDateTimeAsString("yyyy-MM-dd HH:mm:ss");
        System.out.println("Current datetime: " + currentDateTime);

        // ========== Validation ==========
        boolean isValid = DateUtils.isValidDate("25-12-2024", "dd-MM-yyyy");
        System.out.println("Is valid: " + isValid); // true

        boolean isInvalid = DateUtils.isValidDate("32-13-2024", "dd-MM-yyyy");
        System.out.println("Is invalid: " + isInvalid); // false

        // ========== Date arithmetic ==========
        LocalDate futureDate = DateUtils.addDays(LocalDate.now(), 30);
        System.out.println("30 days from now: " + futureDate);

        LocalDate pastDate = DateUtils.subtractDays(LocalDate.now(), 15);
        System.out.println("15 days ago: " + pastDate);

        long daysDiff = DateUtils.getDaysBetween(pastDate, futureDate);
        System.out.println("Days between: " + daysDiff); // 45

        // ========== Date checks ==========
        System.out.println("Is today: " + DateUtils.isToday(LocalDate.now())); // true
        System.out.println("Is future: " + DateUtils.isFuture(futureDate)); // true
        System.out.println("Is past: " + DateUtils.isPast(pastDate)); // true
    }
}
