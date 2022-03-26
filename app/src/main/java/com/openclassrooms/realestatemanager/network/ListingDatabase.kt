package com.openclassrooms.realestatemanager.network

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.Converters
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Listing::class, Photo::class, PointOfInterest::class], version = 1)
@TypeConverters(Converters::class)
abstract class ListingDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao

    companion object {
        @Volatile
        private var INSTANCE: ListingDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ListingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListingDatabase::class.java,
                    "listing_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ListingDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ListingDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.listingDao())
                }
            }
        }

        suspend fun populateDatabase(listingDao: ListingDao) {
            val cal: Calendar = Calendar.getInstance()
            cal.set(2022, 2, 3, 12, 30)
            listingDao.insert(
                Listing(
                    1L,
                    "Appartement",
                    530000,
                    "Paris 18e",
                    62,
                    3,
                    2,
                    1,
                    "Appartement 3 pièces 62m² (église Saint-Bernard-de-la-Chapelle).\n" +
                            "Proche toutes commodités : transports en commun (métro ligne 2 la chapelle - 5 min à pied, RER B et SNCF gare du Nord - 10 min à pied), commerces, restaurants, services médicaux (médecins et hôpital Lariboisière ap-hp).\n" +
                            "Appartement lumineux au 2ème étage, composé d'une entrée spacieuse, de 2 chambres, un séjour avec balcon, une cuisine ouverte, une salle d'eau avec douche et d'une grande buanderie/wc. Beaucoup de rangements : placards dans la buanderie, dans l'entrée, et dans chaque chambre.\n" +
                            "L'appartement est traversant, orienté Est - Ouest .\n" +
                            "Salon côté Est, chambres côté Ouest.",
                    "25 Rue Ordener, 75018 Paris, France",
                    LatLng(48.890798479620095,2.354496030093114),
                    cal,
                    null,
                    "Fred"
                )
            )

            cal.set(2022, 1, 7, 10, 0)
            listingDao.insert(
                Listing(
                    2L,
                    "Studio",
                    329000,
                    "Paris 15e",
                    27,
                    1,
                    1,
                    1,
                    "Studio en très bon état au 7 ème étage d'un immeuble de 10 étages. Vue dégagée sur le Sacré Coeur.\n" +
                            "Salle de bain (avec baignoire) séparée. Cuisine séparée, entrée, pièce principale. Immeuble très bon état. Code, visiophone, double ascenseur. Excellent quartier.",
                    "60 Rue de Vaugirard, 75006 Paris, France",
                    LatLng(48.848609924316406,2.3320999145507812),
                    cal,
                    null,
                    "Fred"
                )
            )

            cal.set(2022, 3, 22, 10, 0)
            listingDao.insert(
                Listing(
                    3L,
                    "Appartement",
                    610000,
                    "Paris 10e",
                    46,
                    3,
                    2,
                    1,
                    "Début de la Rue du faubourg St-Martin, coeur du Village Saint-Martin, appartement traversant de 46m2 au 4ème étage sans ascenseur, sur cours, exposé Est-Ouest, ensoleillé, très calme avec vue dégagée.\n" +
                            "Il est composé d'une cuisine aménagée, d'une pièce de vie ensoleillée exposée Est, une belle chambre ensoleillée, un espace bureau (ou chambre d'enfant) avec rangements et une buanderie avec rangements et machines à laver et sèche linges, d'une salle de douche avec toilettes. HSP 2,65m.\n" +
                            "Une cave saine et fermée complète ce bien.\n" +
                            "Double vitrage, (La porte entre le séjour et la chambre peut être ré-ouverte sans problème).\n" +
                            "Digicode. Interphone. Local à vélo et poussettes\n" +
                            "\n" +
                            "Charges mensuelles environ 115 €.\n" +
                            "Taxe foncière 350€\n",
                    "259 Rue du Faubourg Saint-Martin, 75010 Paris, France",
                    LatLng(48.88337513244997,2.367732254838484),
                    cal,
                    null,
                    "Fred"
                )
            )

            cal.set(2022, 3, 14, 10, 0)
            listingDao.insert(
                Listing(
                    4L,
                    "Appartement",
                    898000,
                    "Paris 11e",
                    90,
                    4,
                    3,
                    1,
                    "Idéalement situé, quartier recherché. Proximité de la place de la Nation. Au pied du métro Avron. Proche de tous commerces, écoles, commerces, ... Dans une résidence des années 60, en bon état. Au 4ème étage sur 7 avec ascenseur.\n" +
                            "\n" +
                            "Appartement 4 pièces de 90 m2 environ, très lumineux : exposé plein sud et traversant, au calme. Il se compose d'une entrée indépendante donnant sur couloir desservant un grand salon / salle à manger (40 m2) avec nombreux rangements et petit balcon, une cuisine équipée et fermée, 3 chambres (avec rangements), une salle de bains avec baignoire et wc ainsi qu'un wc indépendant. Parquet au sol (sauf pièce d'eau : carrelage).\n" +
                            "\n" +
                            "Chauffage collectif. Double vitrage.",
                    "68 Rue de Montreuil, 75011 Paris, France",
                    LatLng(48.85038534262119,2.3912715382877714),
                    cal,
                    null,
                    "Fred"
                )
            )
        }
    }
}