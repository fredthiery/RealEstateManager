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
                    "Appartement 3 pi??ces 62m?? (??glise Saint-Bernard-de-la-Chapelle).\n" +
                            "Proche toutes commodit??s : transports en commun (m??tro ligne 2 la chapelle - 5 min ?? pied, RER B et SNCF gare du Nord - 10 min ?? pied), commerces, restaurants, services m??dicaux (m??decins et h??pital Lariboisi??re ap-hp).\n" +
                            "Appartement lumineux au 2??me ??tage, compos?? d'une entr??e spacieuse, de 2 chambres, un s??jour avec balcon, une cuisine ouverte, une salle d'eau avec douche et d'une grande buanderie/wc. Beaucoup de rangements : placards dans la buanderie, dans l'entr??e, et dans chaque chambre.\n" +
                            "L'appartement est traversant, orient?? Est - Ouest .\n" +
                            "Salon c??t?? Est, chambres c??t?? Ouest.",
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
                    "Studio en tr??s bon ??tat au 7 ??me ??tage d'un immeuble de 10 ??tages. Vue d??gag??e sur le Sacr?? Coeur.\n" +
                            "Salle de bain (avec baignoire) s??par??e. Cuisine s??par??e, entr??e, pi??ce principale. Immeuble tr??s bon ??tat. Code, visiophone, double ascenseur. Excellent quartier.",
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
                    "D??but de la Rue du faubourg St-Martin, coeur du Village Saint-Martin, appartement traversant de 46m2 au 4??me ??tage sans ascenseur, sur cours, expos?? Est-Ouest, ensoleill??, tr??s calme avec vue d??gag??e.\n" +
                            "Il est compos?? d'une cuisine am??nag??e, d'une pi??ce de vie ensoleill??e expos??e Est, une belle chambre ensoleill??e, un espace bureau (ou chambre d'enfant) avec rangements et une buanderie avec rangements et machines ?? laver et s??che linges, d'une salle de douche avec toilettes. HSP 2,65m.\n" +
                            "Une cave saine et ferm??e compl??te ce bien.\n" +
                            "Double vitrage, (La porte entre le s??jour et la chambre peut ??tre r??-ouverte sans probl??me).\n" +
                            "Digicode. Interphone. Local ?? v??lo et poussettes\n" +
                            "\n" +
                            "Charges mensuelles environ 115 ???.\n" +
                            "Taxe fonci??re 350???\n",
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
                    "Id??alement situ??, quartier recherch??. Proximit?? de la place de la Nation. Au pied du m??tro Avron. Proche de tous commerces, ??coles, commerces, ... Dans une r??sidence des ann??es 60, en bon ??tat. Au 4??me ??tage sur 7 avec ascenseur.\n" +
                            "\n" +
                            "Appartement 4 pi??ces de 90 m2 environ, tr??s lumineux : expos?? plein sud et traversant, au calme. Il se compose d'une entr??e ind??pendante donnant sur couloir desservant un grand salon / salle ?? manger (40 m2) avec nombreux rangements et petit balcon, une cuisine ??quip??e et ferm??e, 3 chambres (avec rangements), une salle de bains avec baignoire et wc ainsi qu'un wc ind??pendant. Parquet au sol (sauf pi??ce d'eau : carrelage).\n" +
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