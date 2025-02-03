package com.deliveryboy.delivery.service;

import com.deliveryboy.delivery.model.Delivery;
import com.deliveryboy.delivery.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public void saveDelivery(Delivery delivery) {
        deliveryRepository.save(delivery);
    }
}
