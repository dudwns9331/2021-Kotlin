# Part3 - 7

## Summber Kotlin part3 2021-09-02

<br>

### **_레이아웃 xml_**

Part3 부터는 기본적인 XML 레이아웃에 대한 설명은 생략한다.

<br>

---

### **_Acticity - Kotlin_**

**MainActivity.kt**

```kotlin
package com.example.summer_part3_chapter07

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    // 네이버 맵 API 를 통해서 NaverMap 인스턴스 초기화
    private lateinit var naverMap: NaverMap

    // NAVER Map Android SDK 에 있는 유틸 인스턴스 초기화
    private lateinit var locationSource: FusedLocationSource

    // NAVER Map 을 받아오기 위한 mapView 바인딩
    private val mapView: MapView by lazy {
        findViewById(R.id.mapView)
    }

    // 존재하는 숙소에 대한 정보를 담고 있는 Viewpager 바인딩
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }

    // 여러개의 숙소 Fragemnt 위로 스와이프 했을 때 생기는 RecyclerView 바인딩
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    // NAVER API 에서 제공하는 현재 위치를 찾는 버튼 바인딩
    private val currentLocationButton: LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }

    // 지도 밑에 위쪽으로 스와이프 할 수 있도록 하는 TextView 바인딩
    private val bottomSheetTitleTextView: TextView by lazy {
        findViewById(R.id.bottomSheetTitleTxtView)
    }

    // Viewpager 에 들어가는 데이터를 다루기 위한 HouseViewPagerAdapter
    private val viewPagerAdapter = HouseViewPagerAdapter(itemClicked = {
        // 인텐트를 선언해서 아이템을 클릭했을 때의 처리를 해준다.
        val intent = Intent()
            .apply {
                // intent 보내기
                action = Intent.ACTION_SEND
                putExtra(
                    // 밑에 있는 title, price, imgURL 과 안내 문구를 전송한다.
                    Intent.EXTRA_TEXT,
                    "[지금 이 가격에 예약하세요!!] ${it.title} ${it.price} ${it.imgUrl}"
                )
                type = "text/plain"
            }
        // Android Sharesheet 를 사용하는 경우 Intent.createChooser 를 사용해서 intent 정보를 넘겨준다.
        startActivity(Intent.createChooser(intent, null))
    })

    // RecyclerView 의 데이터를 처리하는 HouseListAdapter 를 넣어준다.
    private val recyclerAdapter = HouseListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // NAVER API 를 통해서 가져오는 mapView 를 초기화 해준다.
        mapView.onCreate(savedInstanceState)
        // getMapAsync 로 비동기 처리를 통해 Naver Map 객체를 얻어온다.
        mapView.getMapAsync(this)

        // 숙소 위에 뜨는 viewPager 의 데이터를 처리하기 위한 어댑터
        viewPager.adapter = viewPagerAdapter

        // 숙소 리스트를 위로 스와이프 했을 때 Fragment에 뜨는 RecyclerView 데이터를 처리하기 위한 어댑터
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        /**
         * ViewPager 에 이전에 추가된 콜백을 제거한다.
         * ViewPager2.OnPageChangeCallback() 오브젝트는 페이지가 변경되거나 점진적으로 스크롤 될 때마다 호출된 콜백을 추가한다.
         * onPageSelected() 함수를 통해서 페이지가 선택되었을 때, viewPagerAdapter 의 최근위치를 받아온다.
         * CameraUpdate 를 통해서 viewPager 에서 선택된 위치로 Map 의 시점을 이동시킨다.
         */
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // 페이지가 선택되었을 때,
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // viewPager 에서 선택된 최근의 위치 정보를 담는다.
                val selectedHouseModel = viewPagerAdapter.currentList[position]
                // selectedHouseModel 의 위도, 경도를 가져오고 CameraUpdate 를 통해서 업데이트 할 위치를 담는다.
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat, selectedHouseModel.lng))
                        .animate(CameraAnimation.Easing)

                // NaverMap API 중 moveCamera 함수를 통해서 최근 위치인 cameraUpdate 를 넣어 시점을 이동시킨다.
                naverMap.moveCamera(cameraUpdate)
            }
        })
    }

    /**
     * onMapReady() 함수는 Naver API 제공 함수이다.
     * 지도가 준비되면 실행되는 함수
     */
    override fun onMapReady(map: NaverMap) {
        // naverMap 에 인스턴스를 초기화 해준다.
        naverMap = map

        // 최대 Zoom
        naverMap.maxZoom = 18.0
        // 최소 Zoom
        naverMap.minZoom = 10.0

        // Map 에 보여지는 시점 최초 업데이트 현재는 춘천 자취방 기준으로 되어 있음.
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.869714, 127.739866))
        naverMap.moveCamera(cameraUpdate)

        // 네이버 API 에서 제공하는 uiSettings 인스턴스를 가져온다.
        val uiSettings = naverMap.uiSettings
        // Floating Button 에 대한 최초 설정을 false 로 한다.
        uiSettings.isLocationButtonEnabled = false
        // 새로 바인딩 한 버튼에 대해서 API 를 넣어준다.
        currentLocationButton.map = naverMap

        // 위치 기능의 활성화를 위해 사용한다. Activity 를 사용하는 생성자이다. LOCATION_PERMISSION_REQUEST_CODE 는 위치 권한 요청 코드
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        // HouseList 에 대한 정보를 Retrofit2 를 통해 가져온다.
        getHouseListFromAPI()
    }

    /**
     * 예약 가능한 숙소 List 를 Retrofit2 를 통해서 가져온다.
     * 해당 정보는 https://run.mocky.io/ 에서 제공하는 형식을 가져온다.
     * JSON 파일은 직접 작성해서 총 5개의 숙소 정보가 들어가 있다.
     */
    private fun getHouseListFromAPI() {
        // retrofit builder 를 통해서 build
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create()) // GsonConverterFactory 를 통해서 변환
            .build()

        // retrofit을 통해서 HouseService 에 정의된 인터페이스를 통해서 통신하도록 한다.
        retrofit.create(HouseService::class.java).also {
            // getHouseList 를 통해서 정보를 가져온다.
            it.getHouseList()
                .enqueue(object :
                    Callback<HouseDto> { // HouseDto 에 정의된 List<HouseModel> 을 Callback 에 넣는다.
                    /**
                     * API 를 통해서 제대로 정보를 가져왔을 때, 응답에 대한 처리 함수
                     */
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        // 성공적으로 가져오지 않은 경우
                        if (response.isSuccessful.not()) {
                            // 종료
                            return
                        }
                        // 성공적으로 가져온 경우
                        response.body()?.let { dto ->
                            // Log.d("Retrofit", dto.toString())
                            // 마커에 대한 정보 업데이트
                            updateMarker(dto.items)
                            // viewPagerAdapter 에 해당 아이템 List 추가
                            viewPagerAdapter.submitList(dto.items)
                            // recyclerAdapter 에 해당 아이템 List 추가
                            recyclerAdapter.submitList(dto.items)

                            // bottomSheetTitleTextView 에 숙소의 개수 표시
                            bottomSheetTitleTextView.text = "${dto.items.size} 개의 숙소"
                        }
                    }

                    // 반응을 아예 받아오지 못하고 실패한 경우
                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {}
                })
        }
    }

    /**
     * 지도에 표시되는 마커, 해당 숙소 JSON 정보에서
     * 위도, 경도에 대한 데이터를 가져와서 마커 표시를 해준다.
     * 마커 표시는 네이버 지도 API 에서 제공해주는 Marker() 를 통해서 해준다.
     */
    private fun updateMarker(houseModel: List<HouseModel>) {
        // houseModel 은 List<HouseModel> 에 저장된 정보를 가져온다.
        houseModel.forEach { house ->
            val marker = Marker()
            // 마커 위치
            marker.position = LatLng(house.lat, house.lng)
            // 마커 클릭 이벤트 처리 this 를 통해서 MainActivity 에서 정의한 onClickListener 를 사용한다.
            marker.onClickListener = this
            // 지도 넣어주기
            marker.map = naverMap
            // 태그
            marker.tag = house.id
            // 마커 아이콘
            marker.icon = MarkerIcons.BLACK
            // 마커 색
            marker.iconTintColor = Color.RED
        }
    }

    /**
     * 권한 정보 요청에 대한 결과 처리 함수
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 만약에 LOCATION_PERMISSION_REQUEST_CODE (위치 권한 요청 코드) 가 아닌 경우
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            // 종료
            return
        }

        // locationSource 에 요청된 권한의 결과가 있는 경우
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            // 활성화 되지 않은 경우
            if (!locationSource.isActivated) {
                //위치 추적모드를 사용하지 않는다.
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    // 앱이 시작할 때, mapView 에 대한 시작도 같이 해준다.
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    // 액티비티의 동작이 다시 시작될 때, mapView 에 대한 재개도 같이 해준다.
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    // 액티비티의 동작이 잠깐 정지할 때, mapView 에 대한 정지도 같이 해준다.
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    // 액티비티 종료될 때 데이터를 저장한다. mapView 의 Bundle 도 같이 저장함
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    // 액티비티의 동작이 멈출 때, mapView 도 같이 멈춰준다.
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    // 액티비티의 동작이 종료될 때, mapView 도 같이 종료한다.
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    // 전체 시스템 메모리가 부족하고 실행중인 메모리 사용량을 줄여야 할 때 호출
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /**
     * onClick 함수 구현 MainActivity 에서
     * Overlay.OnClickListener 를 implement 했기 때문에 구현해야 한다.
     * 해당 함수는 네이버 API 에서 제공하는 함수이다.
     */
    override fun onClick(overlay: Overlay): Boolean {
        val selectedModel = viewPagerAdapter.currentList.firstOrNull() {
            // viewPager 에서 선택한 HouseModel 의 id가 overlay의 tag 값이 같다면 그 값을 저장한다.
            it.id == overlay.tag
        }
        // 선택된 모델에서 position 값을 저장한다.
        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }

    // LOCATION_PERMISSION_REQUEST_CODE 위치 권한 요청 코드 상수 선언
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
```

`Naver Map API` 에서 지도를 가져오고 현재 위치에 대한 정보와 따로 정의한 5개의 숙소에 대해서 `Retrofit2` 를 통해서 가져와서 `marker` 표시를 해준다. 또한, `Viewpager2` 를 통해서 숙소 리스트를 스와이프하여 볼 수 있고 숙소에 따라 map 의 시점이 변경된다. 마지막으로 제일 밑에 있는 숙소 리스트를 위로 스와이프 하여 숙소에 대한 리스트를 볼 수 있다.

<br/>

### **_Adapter - Kotlin_**

**HouseViewPagerAdapter.kt**

```kotlin
package com.example.summer_part3_chapter07

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * 네이버 맵을 보여주는 MainActivity 에서 보여지는 ViewPager 의 데이터를 처리하는 어댑터
 * itemClicked 를 통해서 Item 이 클릭 당했을 때의 이벤트 처리를 받아온다.
 * differ 유틸을 사용한다.
 */
class HouseViewPagerAdapter(val itemClicked: (HouseModel) -> Unit) :
    ListAdapter<HouseModel, HouseViewPagerAdapter.ItemViewHolder>(differ) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        /**
         * titleTextView, priceTextView, thumbnailImageView 에 대한 바인딩을 한다.
         * 받아온 houseModel 에서 title 과 price 를 가져온다.
         * view 가 클릭당했을 때, itemClicked(houseModel) 을 통해서 해당 데이터를 넘겨준다.
         * Glide 를 통해서 이미지를 업로드 한다.
         */
        fun bind(houseModel: HouseModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            view.setOnClickListener {
                itemClicked(houseModel)
            }

            // 썸네일 이미지 업로드
            Glide.with(thumbnailImageView.context)
                .load(houseModel.imgUrl)
                .into(thumbnailImageView)
        }
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            inflater.inflate(
                R.layout.item_house_detail_for_viewpager,
                parent,
                false
            )
        )
    }

    /**
     * ViewHolder 를 bind 한다.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val differ = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
```

Viewpager2 를 통해서 관리되는 데이터를 처리하는 어댑터

<br/>

**HouseListAdapter.kt**

```kotlin
package com.example.summer_part3_chapter07

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * 네이버 맵을 보여주는 MainActivity 에서 보여지는 ViewPager 의 데이터를 처리하는 어댑터
 * itemClicked 를 통해서 Item 이 클릭 당했을 때의 이벤트 처리를 받아온다.
 * differ 유틸을 사용한다.
 */
class HouseListAdapter :
    ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {

    /**
     * ViewHolder RecyclerView : 뷰홀더 내부 클래스로 작성
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        /**
         * titleTextView, priceTextView, thumbnailImageView 에 대한 바인딩을 한다.
         * 받아온 houseModel 에서 title 과 price 를 가져온다.
         * view 가 클릭당했을 때, itemClicked(houseModel) 을 통해서 해당 데이터를 넘겨준다.
         * Glide 를 통해서 이미지를 업로드 한다.
         */
        fun bind(houseModel: HouseModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            // 썸네일 이미지 업로드
            Glide.with(thumbnailImageView.context)
                .load(houseModel.imgUrl)
                .transform(CenterCrop(), RoundedCorners(dpToPx(thumbnailImageView.context, 12)))
                .into(thumbnailImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            inflater.inflate(
                R.layout.item_house,
                parent,
                false
            )
        )
    }

    /**
     * 뷰 홀더가 생성되었을 때,
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    /**
     * 안드로이드 xml 에서 사용하는 dp 를 px 단위로 변환한다.
     */
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 아이템에 대한 처리, DiffUtil 을 통해서
     * 이전의 아이템이 새로운 아이템과 일치하는지 검사한다.
     */
    companion object {
        val differ = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
```

RecyclerView 를 통해서 관리되는 데이터를 처리하는 어댑터

<br/>

### **_HouseDto, Model, Service - Kotlin_**

**HouseDto.kt**

```kotlin
package com.example.summer_part3_chapter07

/**
 * 숙소에 대한 정보를 가지는 데이터 객체를 담는 List
 */
data class HouseDto(
    val items: List<HouseModel>
)

```

<br/>

**HouseModel.kt**

```kotlin
package com.example.summer_part3_chapter07

/**
 * HouseModel 은 Adapter 에서 데이터를 처리하기 위해
 * 사용하는 숙소에 대한 정보이다.
 */
data class HouseModel(
    val id: Int,            // 숙소에 대한 ID
    val title: String,      // 숙소 홍보 제목
    val price: String,      // 숙소 가격
    val imgUrl: String,     // 숙소 이미지
    val lat: Double,        // 위도
    val lng: Double         // 경도
)
```

<br/>

**HouseService.kt**

```kotlin
package com.example.summer_part3_chapter07

import retrofit2.Call
import retrofit2.http.GET

/**
 * retrofit2 를 사용하기 위한 인터페이스
 */
interface HouseService {
    /**
     * HTTP GET 을 통해서 해당 주소에 있는 JSON 데이터를 가져오는 함수
     * getHouseList 는 GET 처리를 통해서 HouseDto 를 Call 해서 가져온다.
     * HouseDto 는 val items: List<HouseModel> 값을 가진다.
     */
    @GET("/v3/e88aa115-d2cf-45ea-80a3-feffdcded4cb")
    fun getHouseList(): Call<HouseDto>

}
```

해당 데이터는 mocky 사이트를 통해서 따로 생성한 JSON 데이터 값을 불러온다.

[mocky 로 간단한 JSON 데이터 서버에 올리기](https://designer.mocky.io/)

<br/>

---

## 참고 문서

#### Naver Map Open API

- 네이버 클라우드 플랫폼

  - https://www.ncloud.com/

- 네이버 Map 사용된 함수 문서

  - https://navermaps.github.io/android-map-sdk/guide-ko/2-2.html

  - https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/util/FusedLocationSource.html

  - https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/overlay/Overlay.OnClickListener.html

  - https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/LocationTrackingMode.html

#### Retrofit2

- Retrofit2 공식 문서

  - https://square.github.io/retrofit/

- Retrofit2 기본 사용법 블로그 참조

  - https://jaejong.tistory.com/33

  - https://velog.io/@hhb041127/AndroidKotlin-Retrofit2%EB%A5%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90

#### ViewPager2, 기타 함수

- ViewPager2

  - https://developer.android.com/reference/androidx/viewpager2/widget/ViewPager2#registerOnPageChangeCallback(androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback)

  - https://developer.android.com/reference/androidx/viewpager2/widget/ViewPager2.OnPageChangeCallback

- 컴포넌트 콜백

  https://developer.android.com/reference/android/content/ComponentCallbacks

---

### 결과화면

<br>

<p align="center">

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap7-result1.PNG" width="360px" height="640px"/>

<img src="https://github.com/dudwns9331/2021-Summer-Kotlin/blob/master/Summer-Technical-Note/assets/part3-chap7-result2.PNG" width="360px" height="640px"/>

</p>

<br>

---

참고문서 - [안드로이드 공식 개발자 페이지](https://developer.android.com/?hl=ko)
