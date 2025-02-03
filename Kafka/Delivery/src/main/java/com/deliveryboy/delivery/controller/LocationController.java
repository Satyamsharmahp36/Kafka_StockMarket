package com.deliveryboy.delivery.controller;

import com.deliveryboy.delivery.model.Delivery;
import com.deliveryboy.delivery.service.KafkaService;
import com.deliveryboy.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private DeliveryService deliveryService;

    private final Map<String, Future<?>> activeSimulations = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    @PostMapping("/update")
    public ResponseEntity<?> updateLocation(
            @RequestParam String name,
            @RequestParam String mobNo,
            @RequestParam String type,
            @RequestParam double distance
    ) {
        try {
            double initialPrice = Double.parseDouble(mobNo);
            if (initialPrice <= 0) {
                throw new IllegalArgumentException("Initial price must be positive");
            }

            String simulationId = UUID.randomUUID().toString();
            stopExistingSimulation(name);

            PriceSimulator simulator = new PriceSimulator(initialPrice);

            Future<?> simulationFuture = executorService.submit(() -> {
                List<String> pricesList = new ArrayList<>();
                try {
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + (long)(distance * 1000);

                    while (!Thread.currentThread().isInterrupted() &&
                            System.currentTimeMillis() < endTime) {

                        double price = simulator.getNextPrice();
                        String formattedPrice = String.format("%.2f", price);
                        pricesList.add(formattedPrice);

                        kafkaService.updateLocation("Stock Update: " + formattedPrice);

                        Thread.sleep(1);
                    }

                    saveDeliveryRecord(name, mobNo, type, distance, pricesList);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    saveDeliveryRecord(name, mobNo, type, distance, pricesList);
                }
            });

            activeSimulations.put(name, simulationFuture);

            return new ResponseEntity<>(Map.of(
                    "message", "Live Stock Simulation Started",
                    "stockName", name,
                    "initialPrice", initialPrice,
                    "exchange", type,
                    "simulationId", simulationId
            ), HttpStatus.OK);

        } catch (NumberFormatException e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Invalid initial price format"
            ), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of(
                    "error", e.getMessage()
            ), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopSimulation(@RequestParam String name) {
        boolean stopped = stopExistingSimulation(name);
        return new ResponseEntity<>(Map.of(
                "message", stopped ? "Simulation stopped" : "No simulation found",
                "stockName", name
        ), HttpStatus.OK);
    }

    private boolean stopExistingSimulation(String stockName) {
        Future<?> existingSimulation = activeSimulations.get(stockName);
        if (existingSimulation != null) {
            existingSimulation.cancel(true);
            activeSimulations.remove(stockName);
            return true;
        }
        return false;
    }

    private void saveDeliveryRecord(String name, String mobNo, String type,
                                    double distance, List<String> pricesList) {
        Delivery delivery = new Delivery();
        delivery.setName(name);
        delivery.setMobNo(mobNo);
        delivery.setType(type);
        delivery.setDistance(distance*1000);
        delivery.setCoordinates(String.join("; ", pricesList));
        deliveryService.saveDelivery(delivery);
    }

    private static class PriceSimulator {
        private final Random random = new Random();
        private double currentPrice;
        private double lastChange = 0;
        private final double initialPrice;

        public PriceSimulator(double initialPrice) {
            this.initialPrice = initialPrice;
            this.currentPrice = initialPrice;
        }

        public double getNextPrice() {
            double volatility = 0.40;

            double randomChange = random.nextGaussian() * volatility;

            double momentum = random.nextDouble() < 0.7 ? lastChange * 0.5 : 0;

            double meanReversion = (initialPrice - currentPrice) * 0.001;

            double totalChange = currentPrice * (randomChange + momentum + meanReversion);

            lastChange = totalChange / currentPrice;
            currentPrice += totalChange;

            double minPrice = initialPrice * 0.7;
            double maxPrice = initialPrice * 1.3;
            currentPrice = Math.min(Math.max(currentPrice, minPrice), maxPrice);

            return currentPrice;
        }
    }
}