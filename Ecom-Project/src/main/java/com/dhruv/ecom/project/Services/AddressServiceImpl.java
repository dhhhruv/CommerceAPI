package com.dhruv.ecom.project.Services;


import com.dhruv.ecom.project.Model.Address;
import com.dhruv.ecom.project.Model.User;
import com.dhruv.ecom.project.Util.AuthUtil;
import com.dhruv.ecom.project.exceptions.ResourceNotFoundException;
import com.dhruv.ecom.project.payload.AddressDTO;
import com.dhruv.ecom.project.repositories.AddressRepository;
import com.dhruv.ecom.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthUtil authUtil;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        List<Address> addressesList = user.getAddresses();
        addressesList.add(address);
        user.setAddresses(addressesList);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressesById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        User currentuser = authUtil.loggedInUser();
        User user = addressFromDatabase.getUser();
        if (!currentuser.equals(user)) {
                throw new ResourceNotFoundException("Address" , "AddressId" , addressId);
        }
            addressFromDatabase.setCity(addressDTO.getCity());
            addressFromDatabase.setPincode(addressDTO.getPincode());
            addressFromDatabase.setState(addressDTO.getState());
            addressFromDatabase.setCountry(addressDTO.getCountry());
            addressFromDatabase.setStreet(addressDTO.getStreet());
            addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

            Address updatedAddress = addressRepository.save(addressFromDatabase);


            user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
            user.getAddresses().add(updatedAddress);
            userRepository.save(user);

            return modelMapper.map(updatedAddress, AddressDTO.class);


    }
    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));


        User currentuser = authUtil.loggedInUser();
        User user = addressFromDatabase.getUser();
        if (!currentuser.equals(user)) {
            throw new ResourceNotFoundException("Address" , "AddressId" , addressId);
        }
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDatabase);

        return "Address deleted successfully with addressId: " + addressId;
    }
}
