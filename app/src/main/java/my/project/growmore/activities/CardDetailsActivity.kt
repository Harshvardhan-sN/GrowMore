package my.project.growmore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.project.growmore.R
import my.project.growmore.databinding.ActivityCardDetailsBinding

class CardDetailsActivity : AppCompatActivity() {
    private var binding: ActivityCardDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}