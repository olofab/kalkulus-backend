package com.kalkulus.repository

import com.kalkulus.model.Offer
import org.springframework.data.jpa.repository.JpaRepository

interface OfferRepository : JpaRepository<Offer, Long>
