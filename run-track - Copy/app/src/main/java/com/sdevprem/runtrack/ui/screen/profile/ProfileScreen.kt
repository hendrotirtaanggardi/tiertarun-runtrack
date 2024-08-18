package com.sdevprem.runtrack.ui.screen.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.ui.nav.Destination
import com.sdevprem.runtrack.ui.utils.component.RunningStatsItem

@Composable
fun ProfileScreen(
    bottomPadding: Dp = 0.dp,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.profileScreenState.collectAsStateWithLifecycle()

    ProfileScreenContent(
        bottomPadding = bottomPadding,
        profileScreenState = state,
        profileEditActions = viewModel,
        navController = navController
    )

    LaunchedEffect(key1 = state.errorMsg) {
        if (state.errorMsg.isNullOrBlank().not())
            Toast.makeText(context, state.errorMsg.toString(), Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun ProfileScreenContent(
    bottomPadding: Dp = 0.dp,
    profileScreenState: ProfileScreenState,
    profileEditActions: ProfileEditActions,
    navController: NavController
) {
    val context = LocalContext.current // Access the context using LocalContext.current

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    val activity = context as? ComponentActivity

    fun showRunInformationDialog(context: Context, title: String, message: String) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_full_screen, null)

        // Set the title and message
        val dialogTitle = view.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = view.findViewById<TextView>(R.id.dialogMessage)
        val positiveButton = view.findViewById<Button>(R.id.positiveButton)

        dialogTitle.text = title
        dialogMessage.text = message

        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(context, R.style.FullScreenDialog)
        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()

        // Set full-screen attributes
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Set the positive button action
        positiveButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    data class RunningRouteItem(
        val gambar: Int,
        val judul: String,
        val lokasi: String,
        val link: String
    )

    fun openGoogleMaps(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    fun showRunningRouteDialog(context: Context, title: String, list: List<RunningRouteItem>) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_full_screen_running_route, null)

        // Set the title
        val dialogTitle = view.findViewById<TextView>(R.id.dialogTitle)
        val positiveButton = view.findViewById<Button>(R.id.positiveButton)
        dialogTitle.text = title

        // Get references to views where you'll dynamically add items
        val container = view.findViewById<LinearLayout>(R.id.container)

        // Iterate through the list and dynamically add views for each route item
        for (item in list) {
            // Inflate item layout for each running route
            val inflater2 = LayoutInflater.from(context)
            val itemView = inflater2.inflate(R.layout.item_running_route, null)

            // Set data for the item
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            val itemTitle = itemView.findViewById<TextView>(R.id.itemTitle)
            val itemLocation = itemView.findViewById<TextView>(R.id.itemLocation)
            val openMapsButton = itemView.findViewById<Button>(R.id.openMapsButton)

            imageView.setImageResource(item.gambar)
            itemTitle.text = item.judul
            itemLocation.text = item.lokasi

            // Handle button click (open Google Maps link)
            openMapsButton.setOnClickListener {
                openGoogleMaps(context, item.link)
            }

            // Add itemView to the container
            container.addView(itemView)
        }

        // Create and show the dialog
        val dialogBuilder = AlertDialog.Builder(context, R.style.FullScreenDialog)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()

        // Set full-screen attributes
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        positiveButton.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    Column {
        TopBar(
            state = profileScreenState,
            profileEditActions = profileEditActions
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_raising_hand),
                        title = "Run Information",
                        onClick = {
                            showRunInformationDialog(context, "Run Information",
                                    "Berolahraga teratur, termasuk lari, meningkatkan kesehatan jantung dan mencegah\n" +
                                    "penyakit tidak menular. Lari dengan intensitas rendah hingga sedang aman dan efektif\n" +
                                    "mencegah penyakit kardiovaskular serta menurunkan risiko kematian. Namun, olahraga\n" +
                                    "intensitas tinggi bisa memicu masalah pada individu dengan penyakit jantung. Berikut\n" +
                                    "beberapa tips lari untuk mencegah efek negatif pada kardiovaskular:\n" +
                                            "\n" +
                                    "1. Hindari lari dengan intensitas tinggi >1 jam secara terus menerut setiap hari.\n" +
                                    "2. Ketika berlari intensitas tinggi, selingi dengan rehat sejenak bagi jantung dengan\n" +
                                    "menurunkan kecepatan lari.\n" +
                                    "3. Lakukan latihan interval tinggi 1-2 kali perminggu, seperti lari ditempat, lompat tali, high\n" +
                                    "kness, butt kicks, dan peregangan dinamis masing masing selama 1 menit. Lalu\n" +
                                    "disambung dengan latihan inti seperti Sprint, Jump Squats, Mountain Climbers, Burpees,\n" +
                                    "dan High kness masing masing selama 30 detik dan diakhiri dengan jalan ditempat atau\n" +
                                    "istirahat selama 1,5 menit.\n" +
                                            "\n" +
                                    "Selain tips tersebut, pelari juga perlu memperhatikan dosis optimal lari bagi\n" +
                                    "dirinya. American College of Sport Medicine merekomendasikan aktivitas fisik intensitas\n" +
                                    "sedang 150 menit/mg atau intensitas tinggi 75 menit/mg untuk kesehatan. Tetapi tidak semua\n" +
                                    "usia memiliki dosis yang sama, Berikut adalah panduan umum untuk dosis lari (jogging) yang\n" +
                                    "optimal berdasarkan kelompok umur:\n" +
                                            "\n" +
                                    "Remaja (13-19 Tahun)\n" +
                                    "1. Jarak: 3-5 km per sesi\n" +
                                    "2. Waktu: 20-30 menit\n" +
                                    "3. Kecepatan: 7-9 km/jam (pace sedang)\n" +
                                    "4. Frekuensi: 3-5 kali per minggu\n" +
                                            "\n" +
                                    "Dewasa Muda (20-29 tahun)\n" +
                                    "1. Jarak: 5-8 km per sesi\n" +
                                    "2. Waktu: 30-45 menit\n" +
                                    "3. Kecepatan: 8-10 km/jam (pace sedang hingga cepat)\n" +
                                    "4. Frekuensi: 3-5 kali per minggu\n" +
                                            "\n" +
                                    "Dewasa (30-49 tahun)\n" +
                                    "1. Jarak: 5-10 km per sesi\n" +
                                    "2. Waktu: 30-60 menit\n" +
                                    "3. Kecepatan: 7-9 km/jam (pace sedang)\n" +
                                    "4. Frekuensi: 3-5 kali per minggu\n" +
                                            "\n" +
                                    "Dewasa Paruh Baya (50-64 tahun)\n" +
                                    "1. Jarak: 3-7 km per sesi\n" +
                                    "2. Waktu: 30-45 menit\n" +
                                    "3. Kecepatan: 6-8 km/jam (pace sedang)\n" +
                                    "4. Frekuensi: 3-4 kali per minggu\n" +
                                            "\n" +
                                    "Lansia (65+ tahun)\n" +
                                    "1. Jarak: 2-5 km per sesi\n" +
                                    "2. Waktu: 20-40 menit\n" +
                                    "3. Kecepatan: 5-7 km/jam (pace lambat hingga sedang)\n" +
                                    "4. Frekuensi: 2-4 kali per minggu\n" +
                                            "\n" +
                                    "Setelah mengetahui dosis lari yang optimal, kita juga perlu meperhatikan pentingnya\n" +
                                    "pemanasan dan pendinginan sebelum dan sesudah berlari. Pemanasan adalah langkah pertama\n" +
                                    "yang penting sebelum berolahraga. Tujuannya adalah untuk mempersiapkan tubuh kita agar\n" +
                                    "siap bergerak dan mencegah cedera. Saat kita pemanasan, aliran darah ke otot meningkat, suhu\n" +
                                    "tubuh naik, dan sendi menjadi lebih fleksibel. Pemanasan sederhana yang bisa pelari lakukan\n" +
                                    "sebelum berlari adalah dengan melakukan peregangan dinamis, berikut adalah macam macam\n" +
                                    "peregangan dinamisnya:\n" +
                                            "\n" +
                                    "1. Lari ditempat (2 menit)\n" +
                                    "Mulai dengan lari di tempat dengan intensitas rendah.\n" +
                                    "Angkat lutut sedikit lebih tinggi dan gerakkan lengan secara bergantian.\n" +
                                            "\n" +
                                    "2. High knees (1 menit)\n" +
                                    "Lari di tempat dengan mengangkat lutut setinggi mungkin.\n" +
                                    "Gerakkan lengan secara ritmis untuk membantu keseimbangan.\n" +
                                            "\n" +
                                    "3. Butt kicks (1 menit)\n" +
                                    "Lari di tempat dengan menendang tumit ke arah bokong.\n" +
                                    "Pastikan gerakan cepat tetapi terkendali.\n" +
                                            "\n" +
                                    "4. Arm cicles (1 menit)\n" +
                                    "Berdiri tegak, rentangkan tangan ke samping.\n" +
                                    "Putar lengan searah jarum jam selama 30 detik, lalu berlawanan arah selama 30\n" +
                                    "detik.\n" +
                                            "\n" +
                                    "5. Leg swings (1 menit)\n" +
                                    "Berdiri di samping dinding atau penyangga untuk keseimbangan.\n" +
                                    "Ayunkan satu kaki ke depan dan belakang sebanyak 10-15 kali.\n" +
                                    "Ulangi dengan kaki yang lain.\n" +
                                            "\n" +
                                    "6. Walking lunges (1 menit)\n" +
                                    "Lakukan langkah panjang ke depan dengan satu kaki, turunkan tubuh hingga kedua\n" +
                                    "lutut membentuk sudut 90 derajat.\n" +
                                    "Bangkit dan lakukan langkah panjang dengan kaki lainnya.\n" +
                                    "Ulangi selama 1 menit.\n" +
                                            "\n" +
                                    "7. Hip circles (1 menit)\n" +
                                    "Berdiri dengan kaki sedikit terbuka.\n" +
                                    "Putar pinggul searah jarum jam selama 30 detik, lalu berlawanan arah selama 30\n" +
                                    "detik.\n" +
                                            "\n" +
                                    "Sekarang kita sudah mengetahui apa yang harus kita lakukan sebelum berlari, sekarang saatnya\n" +
                                    "kita mengetahui pentingnya pendinginan setelah berlari. Pendinginan adalah langkah terakhir\n" +
                                    "yang penting setelah berolahraga. Tujuannya adalah untuk membantu tubuh pulih secara\n" +
                                    "bertahap dari aktivitas fisik. Saat kita pendinginan, denyut jantung dan pernapasan perlahan\n" +
                                    "kembali ke tingkat normal. Berikut adalah contoh pendinginan yang dapat dilakukan setelah\n" +
                                    "berlari:\n" +
                                            "\n" +
                                    "1. Jalan santai (2-3 menit)\n" +
                                    "Kurangi kecepatan lari secara bertahap hingga Anda berjalan dengan santai.\n" +
                                    "Ini membantu menurunkan denyut jantung dan pernapasan secara bertahap.\n" +
                                            "\n" +
                                    "2. Peregangan statis (3-5 menit)\n" +
                                    "Fokus pada peregangan otot yang digunakan saat berlari.\n" +
                                    "a. Hamstring stretch\n" +
                                    "Berdiri dengan kaki selebar bahu.\n" +
                                    "Tekuk badan ke depan, coba sentuh jari-jari kaki.\n" +
                                    "Tahan selama 20-30 detik.\n" +
                                            "\n" +
                                    "b. Quadriceps Stretch\n" +
                                    "Berdiri tegak, pegang pergelangan kaki kanan dengan tangan kanan dan tarik\n" +
                                    "ke arah bokong.\n" +
                                    "Pastikan lutut sejajar dan punggung tetap lurus.\n" +
                                    "Tahan selama 20-30 detik, lalu ganti kaki.\n" +
                                            "\n" +
                                    "c. Calf Stretch\n" +
                                    "Berdiri di depan dinding, letakkan satu kaki di depan dan kaki lainnya di\n" +
                                    "belakang.\n" +
                                    "Tekuk lutut depan sambil menekan tumit kaki belakang ke lantai.\n" +
                                    "Tahan selama 20-30 detik, lalu ganti kaki.\n" +
                                            "\n" +
                                    "d. Hip Flexor Stretch\n" +
                                    "Berdiri dengan satu kaki di depan dan satu kaki di belakang.\n" +
                                    "Tekuk lutut depan dan turunkan pinggul hingga Anda merasakan peregangan\n" +
                                    "di depan pinggul kaki belakang.\n" +
                                    "Tahan selama 20-30 detik, lalu ganti kaki.\n" +
                                            "\n" +
                                    "e. Glute Stretch\n" +
                                    "Duduk di lantai, silangkan kaki kanan di atas kaki kiri yang lurus.\n" +
                                    "Tarik lutut kanan ke arah dada sambil memutar tubuh sedikit ke kanan.\n" +
                                    "Tahan selama 20-30 detik, lalu ganti kaki.\n" +
                                            "\n" +
                                    "f. Upper Body Stretch\n" +
                                    "Shoulder Stretch: Rentangkan satu lengan di depan tubuh, pegang dengan\n" +
                                    "tangan lainnya dan tarik perlahan.\n" +
                                    "Triceps Stretch: Angkat satu lengan ke atas, tekuk siku dan tarik perlahan\n" +
                                    "dengan tangan lainnya.\n" +
                                            "\n" +
                                    "Setelah mengetahui dosis lari yang optimal dan pentingnya melakukan pemanasan serta\n" +
                                    "pendinginan, kita dapat menjaga tubuh tetap bugar dan sehat. Lari dengan dosis yang tepat,\n" +
                                    "ditambah pemanasan yang memadai sebelum berlari dan pendinginan setelahnya, membantu\n" +
                                    "mencegah cedera dan meningkatkan performa. Dengan rutinitas yang baik ini, manfaat\n" +
                                    "kesehatan kardiovaskular dan kebugaran umum dapat tercapai dengan lebih efektif. Ingatlah,\n" +
                                    "konsistensi dan perhatian terhadap detail adalah kunci untuk mendapatkan hasil terbaik dari\n" +
                                    "aktivitas lari. Go Run, Go Healty!")
                        }
                    )

                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_route),
                        title = "Running Route",
                        onClick = {
                            val runningRoutes = listOf(
                                RunningRouteItem(R.drawable.gelora_bung_karno, "Gelora Bung Karno", "Jl. Pintu Satu Senayan, Gelora, Kecamatan Tanah Abang, Kota Jakarta Pusat, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/yrvEQJMGxmXNtA8H7"),
                                RunningRouteItem(R.drawable.taman_catelleya, "Taman Catteleya", "Jl. Letjen S. Parman, RT.15/RW.1, Kemanggisan, Kec. Palmerah, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/s26Tgac3giCiJLyK8"),
                                RunningRouteItem(R.drawable.taman_barito, "Taman Barito", "Kramat Pela, Kec. Kby. Baru, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/nLvrkC4v7wGE1wZc9"),
                                RunningRouteItem(R.drawable.taman_lapangan_banteng, "Lapangan Banteng", "Ps. Baru, Kecamatan Sawah Besar, Kota Jakarta Pusat, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/KWwrhP7h6yLE2hBv8"),
                                RunningRouteItem(R.drawable.taman_rasuna, "Taman Rasuna", "Jl. Taman Patra Kuningan, Kuningan Tim., Kecamatan Setiabudi, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/Rrujb9hZ7D9M51U98"),
                                RunningRouteItem(R.drawable.taman_suropati,"Taman Suropati", "Jl. Jl. Taman Suropati No.5, RT.5/RW.5, Menteng, Kec. Menteng, Kota Jakarta Pusat, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/D4bBqsDL52KbHnSf6"),
                                RunningRouteItem(R.drawable.taman_menteng, "Taman Menteng", "Jl. HOS. Cokroaminoto, RT.3/RW.5, Menteng, Kec. Menteng, Kota Jakarta Pusat, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/D56txgktnyKbanSP6"),
                                RunningRouteItem(R.drawable.kebun_binatang_ragunan, "Kebun Binatang Ragunan", "Ragunan, Ps. Minggu, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/NTe8xFPr8J2ojUyWA"),
                                RunningRouteItem(R.drawable.tebet_eco_park, "Tebet Eco Park", "Jl. Tebet Barat Raya, RT.1/RW.10, Tebet Bar., Kec. Tebet, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta", "https://maps.app.goo.gl/yfhRveRHn2hYuSDA8"),
                                RunningRouteItem(R.drawable.taman_kota_1_bsd, "Taman Kota 1 BSD", "Jl. Letnan Sutopo, Lengkong Gudang Tim., Kec. Serpong, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/Pf99tMJv4ieuZbWK7"),
                                RunningRouteItem(R.drawable.taman_kota_2_bsd, "Taman Kota 2 BSD", "Jl. Letnan Sutopo, Ciater, Kec. Serpong, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/GYLhy2iE3WLLirqz7"),
                                RunningRouteItem(R.drawable.the_breeze_bsd, "The Breeze Jogging Track", "Jl. BSD Green Office Park Jl. BSD Grand Boulevard, Sampora, BSD, Kabupaten Tangerang, Banten", "https://maps.app.goo.gl/WxDep8QAmVeGULLs5"),
                                RunningRouteItem(R.drawable.kebayoran_park, "Kebayoran Park", "Jl. Kby. Residence, Pd. Jaya, Kec. Pd. Aren, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/Wwb5YuUhRdHyYkKB8"),
                                RunningRouteItem(R.drawable.bintaro_boulevard, "Bintaro Boulevard", "Jl. Boulevard Bintaro Jaya, Pd. Jaya, Kec. Pd. Aren, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/SLwqzBYmcsgcd3mg6"),
                                RunningRouteItem(R.drawable.pasific_garden, "Pasific Garden", "Alam Sutera, Jl. Jalur Sutera Bar. No.Kav.19B, RT.002/RW.003, Panunggangan Tim., Kec. Pinang, Kota Tangerang, Banten", "https://maps.app.goo.gl/mUpqyCBcSa3ncnxC9"),
                                RunningRouteItem(R.drawable.downtown_lake_alam_sutera, "Downtown Lake", "Jl. Lingkar Barat, RT.002/RW.003, Panunggangan Tim., Kec. Pinang, Kota Tangerang, Banten", "https://maps.app.goo.gl/4DeXBykN5A4WRtiY9"),
                                RunningRouteItem(R.drawable.the_green_bsd, "The Green Jogging Track", "Jalan Buaran - Rawa Buntu No.51, Cilenggang, Kec. Serpong, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/fPSrWFn93XR2BwNj9"),
                                RunningRouteItem(R.drawable.central_bukit_dago,"Bukit Dago Run Track","Jl. Bukit Dago No.Utama, Rawakalong, Kec. Gn. Sindur, Kabupaten Bogor, Jawa Barat", "https://maps.app.goo.gl/mjq36raEDB3cciVg8"),
                                RunningRouteItem(R.drawable.situ_gintung,"Situ Gintung","Cireundeu Kec. Ciputat Tim, Kota Tangerang Selatan, Banten", "https://maps.app.goo.gl/HTLzgfyPxTYCKaqB7"),
                                RunningRouteItem(R.drawable.kebun_raya_bogor,"Kebun Raya Bogor","Jl. Ir. H. Juanda No.13, Paledang, Kecamatan Bogor Tengah, Kota Bogor, Jawa Barat", "https://maps.app.goo.gl/6wcEuCz5EMwrqoyH8"),
                                RunningRouteItem(R.drawable.pedestrian_kebun_raya_bogor,"Pedestrian Kebun Raya Bogor","Jl. Ir. H. Juanda No.13, Paledang, Kecamatan Bogor Tengah, Kota Bogor, Jawa Barat", "https://maps.app.goo.gl/6wcEuCz5EMwrqoyH8"),
                                RunningRouteItem(R.drawable.taman_kencana,"Taman Kencana","Jl. Jend. Sudirman No.31, RT.01/RW.04, Sempur, Kecamatan Bogor Tengah, Kota Bogor, Jawa Barat", "https://maps.app.goo.gl/Qkm2B3HWUPYjBVGa8"),
                                RunningRouteItem(R.drawable.taman_sempur,"Taman Sempur","Sempur No.1, Sempur, Kecamatan Bogor Tengah, Kota Bogor, Jawa Barat", "https://maps.app.goo.gl/vXdRJZ6NAPNS9Wob8"),
                                RunningRouteItem(R.drawable.telaga_kahuripan,"Telaga Kahuripan","Jl. Telaga Kahuripan Taman Telaga Kahuripan, Tegal, Kec. Kemang, Kabupaten Bogor, Jawa Barat ", "https://maps.app.goo.gl/r65Did75kSdC39WS9"),
                                RunningRouteItem(R.drawable.universitas_indonesia,"Universitas Indonesia","Jl. Lingkar, Pondok Cina, Kecamatan Beji, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/CLjqEKCivGWT4qFa6"),
                                RunningRouteItem(R.drawable.rri_jogging_track,"RRI Jogging Track","Jl. Nusa Dua Raya, Limo, Kec. Limo, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/71je4moM7BJ9EYks7"),
                                RunningRouteItem(R.drawable.telaga_golf,"Telaga Golf","Perumahan, Jl. Telaga Golf Sawangan Raya Jl. Raya Muchtar, Sawangan Lama, Kec. Sawangan, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/y9enhEh7nj9i7ybD7"),
                                RunningRouteItem(R.drawable.the_grove_bojongsari,"The Grove Eco Town","Jl. Raya Bojongsari No.18, Bojongsari Lama, Kec. Bojongsari, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/B9CkFV6wgitC7zng8"),
                                RunningRouteItem(R.drawable.alun_alun_sawangan,"Alun Alun Sawangan","Sawangan Lama, Kec. Sawangan, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/VKQkau8ft43kkKeA7"),
                                RunningRouteItem(R.drawable.villa_rizki,"Villa Rizki","Jl. Vila Rizki Ilhami Jl. Raya Muchtar, Sawangan Lama, Kec. Sawangan, Kota Depok, Jawa Barat", "https://maps.app.goo.gl/bm2Ha5yFt1yJGZLR8"),


                                )

                            showRunningRouteDialog(context, "Running Route", runningRoutes)
                        }
                    )

                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_logout),
                        title = "Logout",
                        showDivider = false,
                        onClick = {
                            googleSignInClient.signOut()
                                .addOnCompleteListener { task: Task<Void?> ->
                                    if (task.isSuccessful) {
                                        // Sign-out was successful
                                        Toast.makeText(context, "Sign out success", Toast.LENGTH_SHORT).show()

                                        navController.navigate(route = Destination.OnBoardingDestination.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }

                                            launchSingleTop = true
                                        }
                                    } else {
                                        val errorMessage = task.exception?.message ?: "Unknown error"
                                        Toast.makeText(context, "Sign out failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.size(bottomPadding + 8.dp))
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    state: ProfileScreenState,
    profileEditActions: ProfileEditActions
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .offset(y = (-24).dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                )
        )
        Column(modifier = modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.size(24.dp))
            TopBarProfile(
                modifier = Modifier.background(color = Color.Transparent),
                user = state.user,
                isEditMode = state.isEditMode,
                profileEditActions = profileEditActions
            )
            Spacer(modifier = Modifier.size(32.dp))
            TotalProgressCard(state = state)
        }
    }

}

@Composable
private fun TotalProgressCard(
    modifier: Modifier = Modifier,
    state: ProfileScreenState,
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Total Progress",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.running_boy),
                unit = "km",
                value = state.totalDistanceInKm.toString()
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
            )
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.stopwatch),
                unit = "hr",
                value = state.totalDurationInHr.toString()
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
            )
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.fire),
                unit = "kcal",
                value = state.totalCaloriesBurnt.toString()
            )
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    img: Painter,
    title: String,
    showDivider: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .clickable(onClick = onClick)
        ) {
            Image(
                painter = img,
                contentDescription = title,
                modifier = Modifier
                    .size(32.dp),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
            )
        }
        if (showDivider)
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.2f
                        )
                    )
                    .align(Alignment.CenterHorizontally)
            )
    }
}
