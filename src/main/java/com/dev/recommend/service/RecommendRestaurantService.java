package com.dev.recommend.service;

import com.dev.recommend.dto.MenuDto;
import com.dev.recommend.dto.RestaurantDto;
import com.dev.recommend.dto.RestaurantListDto;
import com.dev.recommend.entity.Restaurant;
import com.dev.recommend.repository.RestaurantRepository;
import com.dev.utils.response.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.dev.utils.response.BaseResponseStatus.NOT_FOUND_MENU;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendRestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantListDto findRestaurant(String name){
        List<Restaurant> restaurant = restaurantRepository.findRestaurantByFoodName(name);
        if(restaurant.isEmpty()){
            throw new BaseException(NOT_FOUND_MENU);
        }
        List<RestaurantDto> restaurantDtoList = new ArrayList<>();
        for(Restaurant r : restaurant){
            List<MenuDto> menu = restaurantRepository.findMenuByRestaurant(r.getId());
            RestaurantDto restaurantDto = new RestaurantDto(r.getName(),r.getPicture(),r.getLocation(),r.getLatitude(),r.getLongitude(),menu);
            restaurantDtoList.add(restaurantDto);
        }
        return RestaurantListDto.builder().
                restaurantDtoList(restaurantDtoList)
                .build();
    }



}
