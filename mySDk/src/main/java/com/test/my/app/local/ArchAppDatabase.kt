package com.test.my.app.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.EncryptionUtility
import com.test.my.app.local.converter.Converters
import com.test.my.app.local.dao.AppCacheMasterDao
import com.test.my.app.local.dao.DataSyncMasterDao
import com.test.my.app.local.dao.FitnessDao
import com.test.my.app.local.dao.HRADao
import com.test.my.app.local.dao.MedicationDao
import com.test.my.app.local.dao.StoreRecordsDao
import com.test.my.app.local.dao.TrackParameterDao
import com.test.my.app.local.dao.VivantUserDao
import com.test.my.app.model.entity.AppCacheMaster
import com.test.my.app.model.entity.AppVersion
import com.test.my.app.model.entity.DataSyncMaster
import com.test.my.app.model.entity.DocumentType
import com.test.my.app.model.entity.FitnessEntity
import com.test.my.app.model.entity.HRALabDetails
import com.test.my.app.model.entity.HRAQuestions
import com.test.my.app.model.entity.HRASummary
import com.test.my.app.model.entity.HRAVitalDetails
import com.test.my.app.model.entity.HealthDocument
import com.test.my.app.model.entity.MedicationEntity
import com.test.my.app.model.entity.RecordInSession
import com.test.my.app.model.entity.TrackParameterMaster
import com.test.my.app.model.entity.TrackParameters
import com.test.my.app.model.entity.UserRelatives
import com.test.my.app.model.entity.Users
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Users::class, AppVersion::class, HRAQuestions::class, HRAVitalDetails::class, HRALabDetails::class, HRASummary::class, HealthDocument::class, DocumentType::class, DataSyncMaster::class, RecordInSession::class, UserRelatives::class, MedicationEntity.Medication::class, FitnessEntity.StepGoalHistory::class, TrackParameterMaster.Parameter::class, TrackParameterMaster.TrackParameterRanges::class, TrackParameterMaster.History::class, TrackParameters::class, AppCacheMaster::class],
    version = 26,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ArchAppDatabase : RoomDatabase() {

    // DAO
    abstract fun vivantUserDao(): VivantUserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun fitnessDao(): FitnessDao
    abstract fun hraDao(): HRADao
    abstract fun shrDao(): StoreRecordsDao
    abstract fun dataSyncMasterDao(): DataSyncMasterDao
    abstract fun trackParameterDao(): TrackParameterDao
    abstract fun appCacheMasterDao(): AppCacheMasterDao


    companion object {

        val DB_Name = Constants.MAIN_DATABASE_NAME

        fun buildDatabase(context: Context?): ArchAppDatabase {
            return Room.databaseBuilder(context!!, ArchAppDatabase::class.java, DB_Name)
                .fallbackToDestructiveMigration()
                //TODO : Below line is used for DB Security(Encryption and Decryption)
                //To Inspect Database , Comment below line
                .openHelperFactory(
                    SupportFactory(
                        SQLiteDatabase.getBytes(
                            String(
                                EncryptionUtility.IVkey, charset(EncryptionUtility.CHARSET)
                            ).toCharArray()
                        )
                    )
                ).build()
        }
    }
}