package com.l.lolwishlist.utils

import com.l.lolwishlist.data.model.ChampionBase

fun Collection<ChampionBase>.removeThrash() = filterNot {
        it.id.equals("qiyana", true) ||
                it.id.equals("lulu", true) ||
                it.id.equals("lilia", true) ||
                it.id.equals("senna", true) ||
                it.id.equals("akshan", true) ||
                it.id.equals("yuumi", true) ||
                it.id.equals("vex", true) ||
                it.id.equals("seraphine", true)
    }
    .toList()
