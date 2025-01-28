package com.test.my.app.home.ui.aktivo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
//import com.aktivolabs.aktivocore.data.models.challenge.Challenge
import com.test.my.app.R
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentSlidingAktivoChallengesBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingAktivoChallengesFragment : Fragment() {

    private lateinit var binding: FragmentSlidingAktivoChallengesBinding

    private val dateHelper = DateHelper

    companion object {
        var position = 0
        /*var challengesList: MutableList<Challenge> = mutableListOf()

        fun newInstance( pos:Int, list:MutableList<Challenge> ) : SlidingAktivoChallengesFragment {
            val fragment = SlidingAktivoChallengesFragment()
            position = pos
            challengesList = list
            val args = Bundle()
            args.putInt(Constants.POSITION,pos)
            fragment.arguments = args
            return fragment
        }*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingAktivoChallengesBinding.inflate(inflater, container, false)
       /* if ( challengesList.isNotEmpty() ) {
            initialise()
        }*/
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun initialise() {
        /*try {
            val challenge = challengesList[position]
            if (!Utilities.isNullOrEmpty(challenge.imageUrl)) {
                //Glide.with(mContext).load(challenge.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.imgChallenge)
                Picasso.get()
                    .load(challenge.imageUrl)
                    .placeholder(R.drawable.bg_disabled)
                    //.resize(6000, 3000)
                    //.onlyScaleDown()
                    .error(R.drawable.bg_disabled)
                    .into(binding.imgChallenge)
            } else {
                binding.imgChallenge.setImageResource(R.drawable.img_placeholder)
            }

            //binding.btnChallengeType.text = challenge.challengeType
            binding.txtChallengeTitle.text = challenge.title
            binding.txtChallengeDuration.text = "${dateHelper.convertDateSourceToDestination(challenge.startDate, dateHelper.SERVER_DATE_YYYYMMDD, dateHelper.DATEFORMAT_DDMMMYYYY_NEW)
            } - ${
                dateHelper.convertDateSourceToDestination(challenge.endDate, dateHelper.SERVER_DATE_YYYYMMDD, dateHelper.DATEFORMAT_DDMMMYYYY_NEW)
            }"

            //binding.txtDaysLeft.text = "${Days.daysBetween(LocalDate.now(),LocalDate.parse(challenge.endDate)).days} ${context.resources.getString(R.string.DAYS_LEFT)}"
            binding.txtParticipantsCount.text = challenge.numberOfParticipants.toString()
            if (challenge.enrolled) {
                binding.txtStatus.text =resources.getString(R.string.ENROLLED)
                binding.txtStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.state_success
                    )
                )
            } else {
                binding.txtStatus.text = resources.getString(R.string.ENROLL_NOW)
                binding.txtStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textViewColor))
            }

            binding.layoutAktivoChallenges.setOnClickListener {
                CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_CHALLENGES)
                openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                    putString(Constants.CODE, Constants.AKTIVO_CHALLENGES_CODE)
                    putString(Constants.CHALLENGE_ID, challenge.id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

}