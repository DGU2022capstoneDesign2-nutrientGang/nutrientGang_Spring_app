package com.dev.recommend.repository;

import com.dev.recommend.dto.MenuDto;
import com.dev.recommend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @Query("select distinct m.restaurant from Menu m where m.food.name like %:foodName%")
    List<Restaurant> findRestaurantByFoodName(@Param("foodName")String foodName);

    @Query("select new com.dev.recommend.dto.MenuDto(m.food.name,m.description,m.food.calorie,m.food.carbohydrate,m.food.protein,m.food.fat) from Menu m where m.restaurant.id = :resId")
    List<MenuDto> findMenuByRestaurant(@Param("resId")Long id);




}
