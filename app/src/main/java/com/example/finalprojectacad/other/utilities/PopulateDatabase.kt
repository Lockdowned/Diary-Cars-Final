package com.example.finalprojectacad.other.utilities

import com.example.finalprojectacad.data.localDB.dao.CarDao
import com.example.finalprojectacad.data.localDB.entity.BrandRoom
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ModelRoom
import com.example.finalprojectacad.data.localDB.entity.TransmissionRoom

class PopulateDatabase() {

    suspend fun insertDB(carDao: CarDao) {
        brandList.forEach {
            carDao.insertBrand(it)
        }
        modelsList.forEach {
            carDao.insertModel(it)
        }
        transmissionList.forEach {
            carDao.insertTransmission(it)
        }
        carsList.forEach {
            carDao.insertCar(it)
        }
    }


    val brandList = listOf<BrandRoom>(
        BrandRoom("Abarth"),
        BrandRoom("Alfa Romeo"),
        BrandRoom("Aston Martin"),
        BrandRoom("Audi"),
        BrandRoom("Bentley"),
        BrandRoom("BMW"),
        BrandRoom("Bugatti"),
        BrandRoom("Cadillac"),
        BrandRoom("Chevrolet"),
        BrandRoom("Chrysler"),
        BrandRoom("Citroen"),
        BrandRoom("Dacia"),
        BrandRoom("Daewoo"),
        BrandRoom("Daihatsu"),
        BrandRoom("Dodge"),
        BrandRoom("Donkervoort"),
        BrandRoom("DS"),
        BrandRoom("Ferrari"),
        BrandRoom("Fiat"),
        BrandRoom("Fisker"),
        BrandRoom("Ford"),
        BrandRoom("Honda"),
        BrandRoom("Hummer"),
        BrandRoom("Hyundai"),
        BrandRoom("Infiniti"),
        BrandRoom("Iveco"),
        BrandRoom("Jaguar"),
        BrandRoom("Jeep"),
        BrandRoom("Kia"),
        BrandRoom("KTM"),
        BrandRoom("Lada"),
        BrandRoom("Lamborghini"),
        BrandRoom("Lancia"),
        BrandRoom("Land Rover"),
        BrandRoom("Landwind"),
        BrandRoom("Lexus"),
        BrandRoom("Lotus"),
        BrandRoom("Maserati"),
        BrandRoom("Maybach"),
        BrandRoom("Mazda"),
        BrandRoom("McLaren"),
        BrandRoom("Mercedes-Benz"),
        BrandRoom("MG"),
        BrandRoom("Mini"),
        BrandRoom("Mitsubishi"),
        BrandRoom("Morgan"),
        BrandRoom("Nissan"),
        BrandRoom("Opel"),
        BrandRoom("Peugeot"),
        BrandRoom("Porsche"),
        BrandRoom("Renault"),
        BrandRoom("Rolls-Royce"),
        BrandRoom("Rover"),
        BrandRoom("Saab"),
        BrandRoom("Seat"),
        BrandRoom("Skoda"),
        BrandRoom("Smart"),
        BrandRoom("SsangYong"),
        BrandRoom("Subaru"),
        BrandRoom("Suzuki"),
        BrandRoom("Tesla"),
        BrandRoom("Toyota"),
        BrandRoom("Volkswagen"),
        BrandRoom("Volvo"),
    )

    val modelsList = listOf<ModelRoom>(
        ModelRoom(
            "A3",
            4
        ),
        ModelRoom(
            "A4",
            4
        ),
        ModelRoom(
            "e-tron",
            4
        ),
        ModelRoom(
            "Continental GT",
            5
        ),
        ModelRoom(
            "Mulsanne",
            5
        ),
        ModelRoom(
            "Brooklands",
            5
        )
    )

    val transmissionList = listOf<TransmissionRoom>(
        TransmissionRoom("stick"),
        TransmissionRoom("auto"),
        TransmissionRoom("hybrid")
    )

    val carsList = listOf<CarRoom>(
        CarRoom(
            "Nissan",
            "Zxb-34",
            "stick",
            "3Litres",
            year = 5,
            mileage = 325
        )
    )


}