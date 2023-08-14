package com.example.lifeLogger

//static 변수를 담아놓은 클래스
object MyApi {
    const val TAG: String = "로그"
    var Logined_id = ""
    var Logined_name = ""


    //
    // 원격에서 20.200.213.94 접속 C:\IISServer\SHARED_KITCHEN\Monitors 에 저장
    //
    //
    const val selectUser: String = "selectUser.php"
    const val loginUser: String = "loginUser.php"
    const val insertCallInfo: String = "insertCallInfo.php"
    const val insertMessageInfo: String = "insertMessageInfo.php"
    const val insertPictureInfo: String = "insertPictureInfo.php"
    const val insertStepCountInfo: String = "insertStepCountInfo.php"
    const val insertWalkingInfo: String = "insertWalkingInfo.php"
    const val insertSleepInfo: String = "insertSleepInfo.php"
    const val insertHeartRateInfo: String = "insertHeartRateInfo.php"
    const val insertLuxInfo: String = "insertLuxInfo.php"
    const val insertDbInfo: String = "insertDbInfo.php"
    const val insertAppsInfo: String = "insertAppsInfo.php"
    const val deleteAppsInfo: String = "deleteAppsInfo.php"
    const val selectTodayState: String = "selectTodayState.php"
    const val insertTodayStateInfo: String = "insertTodayStateInfo.php"
    const val insertUserInfo: String = "insertUserInfo.php"
    const val selectSurveyList: String = "selectSurveyList.php"
    const val insertSurveyInfo: String = "insertSurveyInfo.php"
    const val selectDataSituation: String = "selectDataSituation.php"
    const val selectMyInfo: String = "selectMyInfo.php"
    const val updateUserInfo: String = "updateUserInfo.php"


}