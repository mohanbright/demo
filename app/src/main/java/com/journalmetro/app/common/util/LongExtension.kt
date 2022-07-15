package com.journalmetro.app.common.util

import android.content.Context
import com.journalmetro.app.R

/**
 * Created by App Developer on August/2021.
 */

private const val idTechnoSection: Int = 19502
private const val idExplorezSection: Int = 418581780

private const val idActualites: Int = 3402
private const val idAhuntsicCartierville: Int = 7131495
private const val idArtisansEtProduitsDici: Int = 418581814
private const val idArtsEtSpectacles: Int = 418581797
private const val idCoteDesNeigesNDG: Int = 48845497
private const val idCulture: Int = 1098
private const val idDebats: Int = 2244
private const val idDossiers: Int = 418581880
private const val idEcrans: Int = 25546070
private const val idEducation: Int = 418580006
private const val idEntrepreneuriat: Int = 418581882
private const val idEnvironnement: Int = 418581826
private const val idesport: Int = 418581966
private const val idEvasionLoisirsEtPleinAir: Int = 418581813
private const val idFinancesPersonnelles: Int = 418582384
private const val idFormationEtEmplois: Int = 418581885
private const val idGuideCadeaux: Int = 418582361
private const val idHabitationEtImmobilier: Int = 418581812
private const val idHochelagaMaisonneuve: Int = 1183294
private const val idIDSverdun: Int = 836056
private const val idInspiration: Int = 418581810
private const val idJeuxDeSociete: Int = 418580228
private const val idJeuxVideo: Int = 418580229
private const val idLachineDorval: Int = 261975153
private const val idLaSalle: Int = 1344652
private const val idLePlateauMontRoyal: Int = 31020994
private const val idLocal: Int = 705
private const val idMjeunes: Int = 418582209
private const val idMangerEtBoireLocal: Int = 418581811
private const val idMercierAnjou: Int = 261974631
private const val idMetroFlirt: Int = 71689006
private const val idMetroXTrente: Int = 418582190
private const val idMobilite: Int = 24261
private const val idMonde: Int = 9136
private const val idMontreal: Int = 7116
private const val idMontrealNord: Int = 1291829
private const val idNational: Int = 10907
private const val idOuestDelile: Int = 66331121
private const val idOutremontMontRoyal: Int = 261974857
private const val idParlonsBacon: Int = 418582383
private const val idParlonsCash: Int = 418582382
private const val idPointeAuxTremblesMontrealEst: Int = 261974547
private const val idPolitique: Int = 418581820
private const val idRencontres: Int = 418581798
private const val idRiviereDesPrairies: Int = 3108379
private const val idRosemont: Int = 5650947
private const val idSaintLaurent: Int = 2085842
private const val idSaintLeonard: Int = 4315464
private const val idSociete: Int = 418581825
private const val idSports: Int = 67
private const val idSudOuest: Int = 1080525
private const val idTechno: Int = 19502
private const val idUncategorized: Int = 1
private const val idVaudreuilSoulanges: Int = 418580614
private const val idVilleMarie: Int = 418581801
private const val idVillerayParcExPetitePatrie: Int = 160390922
private const val idVivreEnsemble: Int = 418581831
private const val idVotePopulaire: Int = 418581088

fun Long?.getSafeLong(): Long {
    return if (this == null) 0L else this
}

// Check that post id equals TechnoSectionId ? It already has null-check.
fun Long?.isIdEqualTechnoSection(): Boolean {
    return if (this == null) false else this.toInt() == idTechnoSection
}

// Check that post id equals ExplorezId (Partner post) ? It already has null-check.
fun Long?.isIdEqualExplorezId(): Boolean {
    return if (this == null) false else this.toInt() == idExplorezSection
}

// Get ads unit id from category id.
fun Long?.getAdsUnitIdFromCategoryId(context: Context?): String {

    // If id or context is null, we can return general ads unit it to show ads in all cases.
    // It has to be hard coded when context comes null.
    if (this == null || context == null) return "/21658289790,22389335471/journalmetro_app/accueil"

    return when(this.toInt()) {
        idActualites -> context.resources.getString(R.string.ads_actualites)
        idAhuntsicCartierville -> context.resources.getString(R.string.ads_local_ahuntsic_cartieville)
        idArtisansEtProduitsDici -> context.resources.getString(R.string.ads_inspiration_artisans_et_produit_dici)
        idArtsEtSpectacles -> context.resources.getString(R.string.ads_culture_art_et_spectacles)
        idCoteDesNeigesNDG -> context.resources.getString(R.string.ads_local_cote_des_neiges_ndg)
        idCulture -> context.resources.getString(R.string.ads_culture_landing)
        idDebats -> context.resources.getString(R.string.ads_debat_landing)
        idEcrans -> context.resources.getString(R.string.ads_culture_ecrans)
        idEducation -> context.resources.getString(R.string.ads_societe_education)
        idEntrepreneuriat -> context.resources.getString(R.string.ads_entrepreneuriat_landing)
        idEnvironnement -> context.resources.getString(R.string.ads_societe_environment)
        idesport -> context.resources.getString(R.string.ads_actualite_sports)
        idEvasionLoisirsEtPleinAir -> context.resources.getString(R.string.ads_inspiration_evasion_loisirs_et_plain_air)
        idExplorezSection -> context.resources.getString(R.string.ads_explorez_Landing)
        idFinancesPersonnelles -> context.resources.getString(R.string.ads_parlons_cash_finances_personnelles)
        idFormationEtEmplois -> context.resources.getString(R.string.ads_entreprise_formation_et_emplois)
        idHabitationEtImmobilier -> context.resources.getString(R.string.ads_inspiration_habitation_et_immobilier)
        idHochelagaMaisonneuve -> context.resources.getString(R.string.ads_local_hochelaga_maisonneuve)
        idIDSverdun -> context.resources.getString(R.string.ads_local_ids_verdun)
        idInspiration -> context.resources.getString(R.string.ads_inspiration_landing)
        idLachineDorval -> context.resources.getString(R.string.ads_local_lachine_dorval)
        idLaSalle -> context.resources.getString(R.string.ads_local_lassalle)
        idLePlateauMontRoyal -> context.resources.getString(R.string.ads_local_Le_plateu_mont_royal)
        idMangerEtBoireLocal -> context.resources.getString(R.string.ads_inspiration_manger_et_boire_local)
        idMercierAnjou -> context.resources.getString(R.string.ads_local_mercier_anjou)
        idMjeunes -> context.resources.getString(R.string.ads_societe_m_jeunes)
        idMobilite -> context.resources.getString(R.string.ads_societe_mobilite)
        idMonde -> context.resources.getString(R.string.ads_actualite_monde)
        idMontreal -> context.resources.getString(R.string.ads_actualite_montreal)
        idLocal -> context.resources.getString(R.string.ads_actualite_montreal)
        idMontrealNord -> context.resources.getString(R.string.ads_local_montreal_nord)
        idNational -> context.resources.getString(R.string.ads_actualite_national)
        idOuestDelile -> context.resources.getString(R.string.ads_local_quest_de_lile)
        idOutremontMontRoyal -> context.resources.getString(R.string.ads_local_outremont_mont_royal)
        idParlonsBacon -> context.resources.getString(R.string.ads_parlons_cash_parlons_bacon)
        idParlonsCash -> context.resources.getString(R.string.ads_parlons_cash_landing)
        idPointeAuxTremblesMontrealEst -> context.resources.getString(R.string.ads_local_point_aux_trembles_montreal_est)
        idPolitique -> context.resources.getString(R.string.ads_actualite_politique)
        idRencontres -> context.resources.getString(R.string.ads_culture_rencontres)
        idRiviereDesPrairies -> context.resources.getString(R.string.ads_local_riviere_des_prairies)
        idRosemont -> context.resources.getString(R.string.ads_local_rosemont)
        idSaintLaurent -> context.resources.getString(R.string.ads_local_saint_laurent)
        idSaintLeonard -> context.resources.getString(R.string.ads_local_saint_leonard)
        idSociete -> context.resources.getString(R.string.ads_societe_landing)
        idSports -> context.resources.getString(R.string.ads_actualite_sports)
        idSudOuest -> context.resources.getString(R.string.ads_local_sud_ouest)
        idTechno -> context.resources.getString(R.string.ads_societe_techno)
        idVaudreuilSoulanges -> context.resources.getString(R.string.ads_local_vaudreuil_soulanges)
        idVilleMarie -> context.resources.getString(R.string.ads_local_ville_marie)
        idVillerayParcExPetitePatrie -> context.resources.getString(R.string.ads_villeray_parc_ex_petite_patrie)
        idVivreEnsemble -> context.resources.getString(R.string.ads_societe_vivre_ensemble)
        idDossiers -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idGuideCadeaux -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idJeuxDeSociete -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idJeuxVideo -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idMetroFlirt -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idMetroXTrente -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idVotePopulaire -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        idUncategorized -> context.resources.getString(R.string.ads_actualites) // These are does not have specific ads unit ids.
        else -> context.resources.getString(R.string.ads_actualites) // Default ads unit id.
    }
}