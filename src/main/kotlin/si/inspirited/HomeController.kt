package si.inspirited

import javax.validation.Valid

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@RequestMapping("/")
class HomeController @Autowired
constructor(private val repository: RecordRepository) {

    @RequestMapping(method = [RequestMethod.GET])
    fun home(model: ModelMap): String {
        val records = repository.findAll()
        model.addAttribute("records", records)
        model.addAttribute("insertRecord", Record())
        return "home"
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun insertData(model: ModelMap,
                   @ModelAttribute("insertRecord") @Valid record: Record,
                   result: BindingResult): String {
        if (!result.hasErrors()) {
            repository.save(record)
        }
        return home(model)
    }
}
