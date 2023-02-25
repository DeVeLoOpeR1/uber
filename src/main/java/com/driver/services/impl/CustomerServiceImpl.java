package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		// Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		// Avoid using SQL query

		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		Customer customer = customerRepository2.findById(customerId).get();
		tripBooking.setCustomer(customer);
		List<Driver> driverList = driverRepository2.findAll();
		Driver driver = null;
		boolean avail = false;
		for(Driver driver1 : driverList){

			Cab cab = driver1.getCab();
			if(cab.getAvailable()){
				avail=true;
				driver = driver1;
				break;
			}

		}
		if(!avail){
			throw new Exception("No value present");
			//throw new Exception("No cab available!");
		}
		System.out.println(driver +" driver available or not please check it there");
		//set driver to trip Booking
		tripBooking.setDriver(driver);

		// set the trip bill amount
		Cab cab = driver.getCab();
		int bill = (cab.getPerKmRate()*distanceInKm);
		tripBooking.setBill(bill);

		// set the cab status to not available
		cab.setAvailable(false);


		// update driver trip list
		driver.getTripBookingList().add(tripBooking);


		//update customer trip list
		customer.getTripBookingList().add(tripBooking);
		
		//customerRepository2.save(customer);


		customerRepository2.save(customer);
		driverRepository2.save(driver);
		return tripBooking;

	}
	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0); // set the bill amount to zero for cancel ride

		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip id and update TripBooking attributes accordingly

		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);

	}
}
