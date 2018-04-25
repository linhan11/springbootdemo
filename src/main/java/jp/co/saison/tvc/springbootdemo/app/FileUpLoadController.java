package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import jp.co.saison.tvc.springbootdemo.domain.form.fileupload.FileUploadForm;


@Controller
public class FileUpLoadController {

	@RequestMapping(value = "/fileupload")
	String fileupload() {
		return "fileupload";
	}

    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    public String fileupload(FileUploadForm fileUploadForm) {
    	MultipartFile mf = fileUploadForm.getFileData();
        System.out.println(mf.getOriginalFilename() + ", " + mf.getSize());

        try {
			System.out.println(mf.getInputStream());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

        return "menu";
    }
}
