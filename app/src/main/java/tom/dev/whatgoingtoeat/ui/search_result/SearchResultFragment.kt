package tom.dev.whatgoingtoeat.ui.search_result

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tom.dev.whatgoingtoeat.databinding.FragmentSearchResultBinding
import tom.dev.whatgoingtoeat.utils.LoadingDialog

@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    private val viewModel: SearchResultViewModel by viewModels()

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchResultListAdapter: SearchResultListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val category by lazy { SearchResultFragmentArgs.fromBundle(requireArguments()).category }

    private var searchByKeywordJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        // FusedLocationClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                startTrackingLocation()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                showPermissionNotGrantedView()
            }
        }

        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchResultListAdapter()

        setSettingButtonClickListener()

        observeLoading()
    }

    private fun setSearchResultListAdapter() {
        searchResultListAdapter = SearchResultListAdapter()

        binding.recyclerviewSearchResult.apply {
            adapter = searchResultListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun startTrackingLocation() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) showPermissionNotGrantedView()

        fusedLocationClient.lastLocation.addOnSuccessListener {
            searchByKeyword(category, it.latitude.toString(), it.longitude.toString())
        }
    }

    private fun showPermissionNotGrantedView() {
        binding.recyclerviewSearchResult.visibility = View.GONE
        binding.progressSearchResult.visibility = View.GONE
        binding.linearlayoutPermissionNotGranted.visibility = View.VISIBLE
    }

    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireContext().packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent -> startActivity(intent) }
    }

    private fun setSettingButtonClickListener() {
        binding.btnPermissionSetting.setOnClickListener {
            goToSettings()
        }
    }

    private fun searchByKeyword(query: String, latitude: String, longitude: String) {
        searchByKeywordJob?.cancel()
        searchByKeywordJob = lifecycleScope.launch {
            viewModel.searchByKeyword(query, latitude, longitude).collectLatest {
                searchResultListAdapter.submitData(it)
            }
        }
    }

    private fun observeLoading() {
        val loading = LoadingDialog(requireContext())
        viewModel.startLoadingDialogEvent.observe(viewLifecycleOwner) {
            loading.show()
        }
        viewModel.stopLoadingDialogEvent.observe(viewLifecycleOwner) {
            loading.dismiss()
        }
    }
}